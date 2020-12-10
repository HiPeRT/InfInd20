package Process;
//Control the running mode of the Traffic light
//Modes: Normal Cycle , blinking yellow and off
import Configs.DevicesConfiguration;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class TrafficLightController {

    private final static Logger logger = LoggerFactory.getLogger(TrafficLightProcess.class);

    private static final String BROKER_ADDRESS = DevicesConfiguration.getBrokerAddress();

    private static final int BROKER_PORT = 1883;



    //Topic used to publish generated demo data
    private static final String TOPIC = "traffic_light/control";
    public static String getTOPIC() {
        return TOPIC;
    }

    private static final int SLEEP_TIME = 1000; /*milliseconds sleep for loop */

    private final static int MAX_ITERATIONS = 90000000;

    private static boolean on = true;

    private static boolean modeChanger=false;

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

        int nIterations=0;
        client.subscribe(Switch.getTOPIC(), new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                on=false;
            }
        });
        client.subscribe(ChangeMode.getTOPIC(), new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                modeChanger =! modeChanger;
            }
        });
        while (nIterations < MAX_ITERATIONS && on ){
            if (!modeChanger) {
                publishData(client, String.format(TOPIC, "/mode"), "1");
                logger.info("Setting mode to normal cycle");
            }
            else {
                publishData(client, String.format(TOPIC, "/mode"), "2");
                logger.info("Setting mode to blinking yellow");
            }
            sleepFor(SLEEP_TIME);
            nIterations++;
        }

        publishData(client, String.format(TOPIC,"/mode"), "0");
        logger.info("Turning OFF Traffic Light Processes");
    }




    private static void sleepFor(int sleepTime) {
        try { Thread.sleep (SLEEP_TIME);
        }
        catch (InterruptedException ex) {logger.error("Error in CONTROLLER sleep");}
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

}