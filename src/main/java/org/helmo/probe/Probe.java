package org.helmo.probe;

public abstract class Probe {
    protected String servicesURL;
    protected int pollingInterval;

    public Probe(String servicesURL, int pollingInterval) {
        this.servicesURL = servicesURL;
        this.pollingInterval = pollingInterval;
    }

    public abstract void start();
    public abstract void stop();
    protected abstract void collectData();
}