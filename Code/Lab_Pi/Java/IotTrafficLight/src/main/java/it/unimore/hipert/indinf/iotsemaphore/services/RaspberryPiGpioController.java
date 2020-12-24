package it.unimore.hipert.indinf.iotsemaphore.services;

import it.unimore.hipert.indinf.iotsemaphore.model.SemaphoreColors;

public class RaspberryPiGpioController implements ILightsController{
    @Override
    public void changeLightColors(SemaphoreColors currentState) {
        System.out.println("Now lights are " + currentState);
    }
}
