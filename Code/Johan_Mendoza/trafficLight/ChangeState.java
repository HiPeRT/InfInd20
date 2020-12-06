package iot.traffic;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ChangeState {
    private final static Logger logger = LoggerFactory.getLogger(AuthConsumer.class);

    //IP Address of the target MQTT Broker
    private static String BROKER_ADDRESS = "155.185.228.19";

    //PORT of the target MQTT Broker
    private static int BROKER_PORT = 7883;

    //MQTT account username to connect to the target broker
    private static final String MQTT_USERNAME = "253299";

    //MQTT account password to connect to the target broker
    private static final String MQTT_PASSWORD = "qyrutpae";

    //Basic Topic used to consume generated demo data (the topic is associated to the user)
    private static final String MQTT_BASIC_TOPIC = "/iot/user/253299/actuator/traffic/";

    private static final String CONTROL_TOPIC = "control";

    public static void main(String [ ] args) {

        logger.info("MQTT Auth Consumer Tester Started ...");

        try{

            //Generate a random MQTT client ID using the UUID class
            String clientId = UUID.randomUUID().toString();

            //Represents a persistent data store, used to store outbound and inbound messages while they
            //are in flight, enabling delivery to the QoS specified. In that case use a memory persistence.
            //When the application stops all the temporary data will be deleted.
            MqttClientPersistence persistence = new MemoryPersistence();

            //The the persistence is not passed to the constructor the default file persistence is used.
            //In case of a file-based storage the same MQTT client UUID should be used
            IMqttClient client = new MqttClient(
                    String.format("tcp://%s:%d", BROKER_ADDRESS, BROKER_PORT),
                    clientId,
                    persistence);

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

            //public the data to control channel

            publishData(client, MQTT_BASIC_TOPIC + CONTROL_TOPIC, "4" );

            client.disconnect();
            client.close();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void publishData(IMqttClient mqttClient, String topic, String msgString) throws MqttException {

        logger.debug("Publishing to Topic: {} Data: {}", topic, msgString);

        if (mqttClient.isConnected() && msgString != null && topic != null) {

            MqttMessage msg = new MqttMessage(msgString.getBytes());
            msg.setQos(0);
            msg.setRetained(false);
            mqttClient.publish(topic,msg);

            logger.debug("(If Authorized by Broker ACL) Data Correctly Published !");
        }
        else{
            logger.error("Error: Topic or Msg = Null or MQTT Client is not Connected !");
        }

    }

}

