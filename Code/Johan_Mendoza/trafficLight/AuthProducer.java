package iot.traffic;


import com.pi4j.io.gpio.*;
import iot.traffic.TrafficLight;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Simple MQTT Producer using the library Eclipse Paho
 * and authentication credentials
 *
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project mqtt-playground
 * @created 14/10/2020 - 09:19
 */
public class AuthProducer {

    private final static Logger logger = LoggerFactory.getLogger(AuthProducer.class);

    //BROKER URL
    private static String BROKER_URL = "tcp://155.185.228.19:7883";

    //Message Limit generated and sent by the producer
    private static final int MESSAGE_COUNT = 1000;

    //MQTT account username to connect to the target broker
    private static final String MQTT_USERNAME = "253299";

    //MQTT account password to connect to the target broker
    private static final String MQTT_PASSWORD = "qyrutpae";

    //Basic Topic used to publish generated demo data (the topic is associated to the user)
    private static final String MQTT_BASIC_TOPIC = "/iot/user/253299/";

    //Additional Topic structure used to publish generated demo data. It is merged with the Basic Topic to obtain
    //the final used topic
    private static final String TOPIC = "actuator/traffic";

    private static final String CONTROL_TOPIC = "control";

    public static void main(String[] args) {

        logger.info("Auth SimpleProducer started ...");

        try{

            //Generate a random MQTT client ID using the UUID class
            String publisherId = UUID.randomUUID().toString();

            //Represents a persistent data store, used to store outbound and inbound messages while they
            //are in flight, enabling delivery to the QoS specified. In that case use a memory persistence.
            //When the application stops all the temporary data will be deleted.
            MqttClientPersistence persistence = new MemoryPersistence();

            //The the persistence is not passed to the constructor the default file persistence is used.
            //In case of a file-based storage the same MQTT client UUID should be used
            IMqttClient client = new MqttClient(BROKER_URL, publisherId, persistence);

            //Define MQTT Connection Options such as reconnection, persistent/clean session and connection timeout
            //Authentication option can be added -> See AuthProducer example
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(MQTT_USERNAME);
            options.setPassword(new String(MQTT_PASSWORD).toCharArray());
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            //Connect to the target broker
            client.connect(options);

            logger.info("Connected !");

            //create gpio instance

            GpioController gpio = GpioFactory.getInstance();
            GpioPinDigitalOutput[] pins = {
                    gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, PinState.LOW),
                    gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, PinState.LOW),
                    gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, PinState.LOW)};

            //Create an instance of an Engine Temperature Sensor
            TrafficLight trafficLight = new TrafficLight(pins);

            // set shutdow state for this pin
            gpio.setShutdownOptions(true, PinState.LOW, trafficLight.getPins());

            //sub to control channel
            registerToControlChannel(client, MQTT_BASIC_TOPIC, publisherId, trafficLight);

            //Start to publish MESSAGE_COUNT messages
            int count = 0;
            int state = 7;
            while (true) {

                //Send data as simple numeric value
                int trafficValue = trafficLight.getCurrentState();
                String payloadString = Integer.toString(trafficValue);

                //fake error
                //count time led red
                if(trafficValue == 0) {
                    count++;
                }

                if (count >= 4) {
                    trafficLight.setCurrentState(4);
                    int c = 0;
                    while (c < 5){
                        trafficLight.BlinkingYellow(trafficLight.getCurrentState());
                        publishData(client, MQTT_BASIC_TOPIC + TOPIC, Integer.toString(trafficLight.getCurrentState()));
                        c++;
                    }
                    break;
                }

                //control if the current state == 4 or 5
                if (trafficLight.getCurrentState() == 4 || trafficLight.getCurrentState() == 5){
                    int i = 0;
                    while(i < 5){
                        trafficLight.BlinkingYellow(trafficLight.getCurrentState());
                        publishData(client, MQTT_BASIC_TOPIC + TOPIC, Integer.toString(trafficLight.getCurrentState()));
                        i++;
                    }
                    break;
                }

                //Internal Method to publish MQTT data using the created MQTT Client
                //The final topic is obtained merging the MQTT_BASIC_TOPIC and TOPIC in order to send the messages
                //to the correct topic root associated to the authenticated user
                //Eg. /iot/user/000001/sensor/temperature
                if(trafficLight.getCurrentState() != state){ //public only if the statu changed
                    publishData(client, MQTT_BASIC_TOPIC + TOPIC, payloadString);
                    state = trafficLight.getCurrentState();
                }


                //MAKE START THE NORMAL CYCLE
                logger.info("Currente State: {}",trafficLight.getCurrentState());
                trafficLight.NormalCycle(trafficLight.getCurrentState());


                //CONTROL ERROR
                if (trafficLight.getCurrentState() == -1){
                    logger.error("invalid input");
                    break;
                }

                //Sleep for 1 Second
               Thread.sleep(1000);
            }

            //Disconnect from the broker and close the connection
            client.disconnect();
            client.close();
            gpio.shutdown();

            logger.info("Disconnected !");

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Send a target String Payload to the specified MQTT topic
     *
     * @param mqttClient
     * @param topic
     * @param msgString
     * @throws MqttException
     */
    public static void publishData(IMqttClient mqttClient, String topic, String msgString) throws MqttException {

        logger.debug("Publishing to Topic: {} Data: {}", topic, msgString);

        if (mqttClient.isConnected() && msgString != null && topic != null) {
        	
            MqttMessage msg = new MqttMessage(msgString.getBytes());
            msg.setQos(0);
            msg.setRetained(true);
            mqttClient.publish(topic,msg);
            
            logger.debug("(If Authorized by Broker ACL) Data Correctly Published !");
        }
        else{
            logger.error("Error: Topic or Msg = Null or MQTT Client is not Connected !");
        }

    }

    public static void registerToControlChannel(IMqttClient mqttClient, String topic, String publisherId, TrafficLight trafficLight) {

        try{

            String deviceControlTopic = String.format("%s%s/%s", topic, TOPIC, CONTROL_TOPIC);

            logger.info("Registering to Control Topic ({}) ... ", deviceControlTopic);

            mqttClient.subscribe(deviceControlTopic, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {

                    if(message != null) {
                        logger.info("[CONTROL CHANNEL] -> Control Message Received -> {}", new String(message.getPayload()));
                        String mess = new String(message.getPayload());
                        trafficLight.setCurrentState(Integer.parseInt(mess));
                    }else {
                        logger.error("[CONTROL CHANNEL] -> Null control message received !");
                    }
                }
            });

        }catch (Exception e){
            logger.error("ERROR Registering to Control Channel ! Msg: {}", e.getLocalizedMessage());
        }
    }



}
