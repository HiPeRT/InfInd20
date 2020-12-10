package Devices;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;


public abstract class GenericLed <t>{
    private String color;
    private int ledPortOnPi ;
    private boolean On;

    public GenericLed (){
    }

    public String getColor() {
        return color;
    }

    void setOn(){
        On = true;
    }

    void setOff(){
        On=false;
    }

    public abstract void TurnOnLed();

    public abstract  void TurnOffLed();

    public boolean getOn(){
        return On;
    };

    @Override
    public String toString() {
        return "GenericLed{" +
                "color='" + color + '\'' +
                ", ledPortOnPi=" + ledPortOnPi +
                ", On=" + On +
                '}';
    }
}
