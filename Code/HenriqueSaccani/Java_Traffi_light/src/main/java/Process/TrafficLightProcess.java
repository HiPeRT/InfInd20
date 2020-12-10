package Process;
/*
    Must run on a Raspberry device

    Author : Henrique Saccani
    University:UNIMORE
    Courses: "Programmazione industriale" and " Intelligent Internet Of Things"
    year: 2020
 */

import Configs.DevicesConfiguration;
import Devices.GreenLed;
import Devices.RedLed;
import Devices.YellowLed;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class TrafficLightProcess{

    private final static Logger logger = LoggerFactory.getLogger(TrafficLightProcess.class);

    private final static String BROKER_ADDRESS = DevicesConfiguration.getBrokerAddress();

    private final static int BROKER_PORT = 1883;

    //Topic used to publish generated demo data
    private static final String TOPIC = "traffic_light/process";

    private static int mode = 0; //STARTING IN OFF

    private final static int GREEN_TO_YELLOW_TIME = 2000; /* milliseconds */
    private final static int RED_TO_GREEN_TIME = 3000; /* milliseconds */
    private final static int YELLOW_TO_RED_TIME = 1000; /* milliseconds */
    private final static int MAX_ITERATIONS = 3000000;

    private static GreenLed green = new GreenLed();
    private static RedLed red = new RedLed();
    private static YellowLed yellow=new YellowLed();

    public static void main(String[] args) throws MqttException {
        logger.info("Traffic light Started...");
        String mqttClientId = UUID.randomUUID().toString();

        MqttClientPersistence persistence = new MemoryPersistence();

        MqttClient client = new MqttClient(String.format("tcp://%s:%d", BROKER_ADDRESS, BROKER_PORT),mqttClientId, persistence);


        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);

        /* Connect to the target broker */
        client.connect(options);
        logger.info("Connected ! Client Id: {}", mqttClientId);
        int i = 0;
        int msgCounter=0;/* to know what led to turn on */
        boolean blinker=false;
        client.subscribe(String.format(TrafficLightController.getTOPIC(), "/mode"), new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if (message != null) try {
                    if (mode!=Integer.parseInt(new String(message.getPayload()))) {
                        logger.info("CONTROLLER MESSAGE FROM TOPIC {} with code {}", topic, new String(message.getPayload()));
                        mode = Integer.parseInt(new String(message.getPayload()));
                    }
                } catch (Exception e) {
                    logger.error("ERROR Registering to Control Channel ! Msg: {}", e.getLocalizedMessage());
                }
            }
        });
        while (msgCounter <= MAX_ITERATIONS) {
            if (mode == 1) {
                if (i == 0) { //red Led
                    publishData(client, String.format(TOPIC, "/mode"), "Normal_Cycle");
                    publishData(client, String.format(TOPIC, "/led_on"), "red");
                    red.TurnOnLed();
                    ledActiveTIme(RED_TO_GREEN_TIME);
                    red.TurnOffLed();
                }
                if (i == 1) { // green led
                    publishData(client, String.format(TOPIC, "/mode"), "normal_cycle");
                    publishData(client, String.format(TOPIC, "/led_on"), "green");
                    green.TurnOnLed();
                    ledActiveTIme(GREEN_TO_YELLOW_TIME);
                    green.TurnOffLed();
                }
                if (i == 2) { // yellow led
                    publishData(client, String.format(TOPIC, "/mode"), "normal_cycle");
                    publishData(client, String.format(TOPIC, "/led_on"), "yellow");
                    yellow.TurnOnLed();
                    ledActiveTIme(YELLOW_TO_RED_TIME);
                    yellow.TurnOffLed();
                }
            }
            if (mode == 2) {
                publishData(client, String.format(TOPIC, "/mode"), "blinking_yellow");
                if (blinker) { // All off
                    publishData(client, String.format(TOPIC, "/led_on"), "none");
                    ledActiveTIme(YELLOW_TO_RED_TIME);
                } else { //Yellow on
                    publishData(client, String.format(TOPIC, "/led_on"), "yellow");
                    yellow.TurnOnLed();
                    ledActiveTIme(YELLOW_TO_RED_TIME);
                    yellow.TurnOffLed();
                }
                blinker = !blinker;
            }
            if (mode != 0 && mode != 1 && mode != 2) {
                logger.error("Error in mode variable, unknown mode in Traffic light id: {} going on blinking yellow mode...", mqttClientId);
                mode = 2;
            }

            if (mode == 0) {
                logger.info("The traffic light is off");
                ledActiveTIme(RED_TO_GREEN_TIME);
            }
            i++;
            msgCounter++;
            if (i >= 3 ){
                i=0;
            }
        }

        publishData(client, TOPIC, "Bye Bye");
        logger.info("Turning off Client ID {}",mqttClientId);

    }


    public static void publishData(IMqttClient mqttClient, String topic, String msgString) throws MqttException {

        logger.debug("Publishing to Topic: {} Data: {}", topic, msgString);

        if (mqttClient.isConnected() && msgString != null && topic != null) {

            //Create an MQTT Message defining the required QoS Level and if the message is retained or not
            MqttMessage msg = new MqttMessage(msgString.getBytes());
            msg.setQos(0);
            msg.setRetained(false);
            mqttClient.publish(topic,msg);

            logger.debug("Data Correctly Published !");
        }
        else{
            logger.error("Error: Topic or Msg = Null or MQTT Client is not Connected !");
        }

    }

    private static void ledActiveTIme(int timeActive){
        try { Thread.sleep (timeActive);
        }
        catch (InterruptedException ex) {logger.error("Error in ledActiveTime function");}
    }

}