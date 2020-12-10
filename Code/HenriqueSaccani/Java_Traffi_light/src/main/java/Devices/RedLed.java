package Devices;

import com.pi4j.io.gpio.*;

public class RedLed extends GenericLed {
    final GpioController gpio = GpioFactory.getInstance();
    final GpioPinDigitalOutput led1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00);
    private final String color="red";

    @Override
    public void TurnOnLed() {
        led1.high();
        setOn();
    }

    @Override
    public void TurnOffLed() {
        led1.low();
        setOff();
    }

}
