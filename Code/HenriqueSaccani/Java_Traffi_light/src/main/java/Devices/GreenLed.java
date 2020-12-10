package Devices;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;

public class GreenLed extends GenericLed{
    final GpioController gpio = GpioFactory.getInstance();
    final GpioPinDigitalOutput led1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03);
    private final String color="green";


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
