package iot.traffic;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple MQTT Consumer using the library Eclipse Paho
 * and authentication credentials
 *
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project mqtt-playground
 * @created 14/10/2020 - 09:19
 */
public class AuthConsumer {

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
    private static final String MQTT_BASIC_TOPIC = "/iot/user/253299/";

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


            //Subscribe to the target topic #. In that case the consumer will receive (if authorized) all the message
            //passing through the broker
            client.subscribe(MQTT_BASIC_TOPIC + "#", (topic, msg) -> {

            	byte[] payload = msg.getPayload();
            	String state = new String(payload);
            	logger.info("Message Received ({}) Message Received: {}", topic, new String(payload));
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
