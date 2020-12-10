package Configs;

public class DevicesConfiguration {
    ///just to take track of the ports
    private static final int portRedLed=0;
    private static final int portYellowLed=2;
    private static final int portGreenLed=3;
    private static final String BROKER_ADDRESS = "192.168.1.114";


    public static String getBrokerAddress() {
        return BROKER_ADDRESS;
    }

    public int getPortRedLed() {
        return portRedLed;
    }

    public int getPortYellowLed() {
        return portYellowLed;
    }

    public int getPortGreenLed() {
        return portGreenLed;
    }

}
