package it.unimore.hipert.indinf.iotsemaphore.services;

/**
 * The FSM for a "normal" Italian semaphore: Red -> Yellow -> Green. Its timings are fetch by configuration service
 */
public class ItalianSemaphore implements ISemaphoreFsm {

    public ItalianSemaphore(IConfiguration configuration) {

    }

    @Override
    public void changeState() {

    }
}
