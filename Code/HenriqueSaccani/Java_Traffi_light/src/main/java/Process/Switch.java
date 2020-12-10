package Process;
//Turn off the traffic light
import Configs.DevicesConfiguration;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class Switch {
    private final static Logger logger = LoggerFactory.getLogger(TrafficLightProcess.class);

    private static String BROKER_ADDRESS = DevicesConfiguration.getBrokerAddress();

    private static int BROKER_PORT = 1883;

    //Topic used to publish generated demo data
    private static final String TOPIC = "traffic_light/switch";
    public static String getTOPIC() {
        return TOPIC;
    }

    private static boolean switcher = false;

    public static void main(String[] args) throws MqttException {
        logger.info("Traffic light Started...");
        String mqttClientId = UUID.randomUUID().toString();

        MqttClientPersistence persistence = new MemoryPersistence();

        MqttClient client = new MqttClient(String.format("tcp://%s:%d", BROKER_ADDRESS, BROKER_PORT), mqttClientId, persistence);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);

        /* Connect to the target broker */
        client.connect(options);
        //logger.info("Connected ! Client Id: {}", mqttClientId);
        publishData(client,TOPIC,"false");
    }
    public static void publishData(IMqttClient mqttClient, String topic, String msgString) throws MqttException {

            logger.debug("Publishing to Topic: {} Data: {}", topic, msgString);

            if (mqttClient.isConnected() && msgString != null && topic != null) {

                //Create an MQTT Message defining the required QoS Level and if the message is retained or not
                MqttMessage msg = new MqttMessage(msgString.getBytes());
                msg.setQos(0);
                msg.setRetained(false);
                mqttClient.publish(topic, msg);

                logger.debug("Data Correctly Published !");
            } else {
                logger.error("Error: Topic or Msg = Null or MQTT Client is not Connected !");
            }
        }
    }

