package iot.traffic;
import com.pi4j.io.gpio.GpioController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.pi4j.util.CommandArgumentParser;

public class TrafficLight extends Thread {

    //LED RED --> PIN 1, LED GREEN --> PIN 0, LED YELLOW --> PIN 2
    private static final Logger logger = LoggerFactory.getLogger(TrafficLight.class);
    public int TIME1 = 10;
    public int TIME2 = 15;
    public int TIME3 = 5;

    private int currentState;

    private GpioPinDigitalOutput[] pins;

    public TrafficLight(){
        this.currentState = 0;
    }

    public TrafficLight(GpioPinDigitalOutput[] pins){ this.pins = pins; this.currentState = 0;}

    public void NormalCycle(int state) throws InterruptedException {
        switch (state){
            case 0:
                this.currentState = 2;
                allLedOffExcept(0);
                logger.info("New State: {}", this.currentState);
                Thread.sleep(TIME1*1000);
                break;
            case 1:
                this.currentState = 0;
                allLedOffExcept(1);
                logger.info("New State: {}", this.currentState);
                Thread.sleep(TIME3*1000);
                break;
            case 2:
                this.currentState = 1;
                allLedOffExcept(2);
                logger.info("New State: {}", this.currentState);
                Thread.sleep(TIME2*1000);
                break;
            default:
                this.currentState = -1;
        }
    }

    public void BlinkingYellow(int state) throws InterruptedException {
        this.currentState = state;

        switch (this.currentState){
                case 4:
                    this.currentState = 5;
                    allLedOffExcept(2);
                    logger.info("New State: {}", this.currentState);
                    Thread.sleep(2000);
                    break;
                case 5:
                    this.currentState = 4;
                    allLedOff();
                    logger.info("New State: {}", this.currentState);
                    Thread.sleep(2000);
                    break;
            }

    }

    private void setLed(int lednumber, boolean value){
        if (value){
            this.pins[lednumber].high();
        } else {
            this.pins[lednumber].low();
        }
    }

    //set all led off except one
    private void allLedOffExcept(int lednumber){
        for (int i=0; i<this.pins.length; i++) {
            if (this.pins[i] == this.pins[lednumber]){
                System.out.println("Led :" + i +" ON");
                setLed(i, true);
            } else{
                setLed(i, false);
            }
        }
    }

    //set all led off
    private void allLedOff(){
        for (int i =0; i<this.pins.length; i++){setLed(i,false);}
    }

    public int getCurrentState() { return currentState; }

    public void setCurrentState(int currentState) { this.currentState = currentState; }

    public GpioPinDigitalOutput[] getPins() {
        return pins;
    }

    public static void main(String[] args) throws InterruptedException {

        GpioController gpio = GpioFactory.getInstance();

        GpioPinDigitalOutput[] pins = {
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, PinState.LOW),
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, PinState.LOW),
                gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, PinState.LOW)};

        TrafficLight trafficLight = new TrafficLight(pins);

        // set shutdow state for this pin
        gpio.setShutdownOptions(true, PinState.LOW, trafficLight.pins);

        int count = 0;
        while (true){

            //fake error
            //count time led red
            if(trafficLight.getCurrentState() == 0) {
                count++;
            }

            if (count >= 4) {
                trafficLight.setCurrentState(4);
                trafficLight.BlinkingYellow(trafficLight.getCurrentState());
                break;
            }


            logger.info("Currente State: {}",trafficLight.getCurrentState());
            trafficLight.NormalCycle(trafficLight.getCurrentState());


            if (trafficLight.getCurrentState() == -1){
                logger.error("invalid input");
                break;
            }



        }

    }

}
