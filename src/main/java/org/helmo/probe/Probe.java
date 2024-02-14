package org.helmo.probe;

import java.util.List;

public abstract class Probe {
    protected String servicesURL;
    protected int pollingInterval;
    protected List<Aurl> urls;

    public Probe(String servicesURL, int pollingInterval) {
        this.servicesURL = servicesURL;
        this.pollingInterval = pollingInterval;
    }

    public abstract void start();
    public abstract void stop();
    protected abstract void collectData();
}
