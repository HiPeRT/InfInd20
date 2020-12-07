package it.unimore.hipert.indinf.iotsemaphore.process;

import it.unimore.hipert.indinf.iotsemaphore.model.SemaphoreConfigurationMessage;
import it.unimore.hipert.indinf.iotsemaphore.services.GsonParser;
import it.unimore.hipert.indinf.iotsemaphore.services.IConfiguration;
import it.unimore.hipert.indinf.iotsemaphore.services.IJsonParser;
import it.unimore.hipert.indinf.iotsemaphore.services.StaticConfiguration;

public class MainClass {
    public static void main(String[] args) {
        IConfiguration config = new StaticConfiguration();
        IJsonParser<SemaphoreConfigurationMessage> jsonParser = new GsonParser<>(SemaphoreConfigurationMessage.class);

        MqttReceiver mqttReceiver = new MqttReceiver(config, jsonParser);
        mqttReceiver.start();

    }
}
