package org.helmo;

public abstract class Probe {
    protected String servicesURL;
    protected int pollingInterval;

    public Probe(String servicesURL, int pollingInterval) {
        this.servicesURL = servicesURL;
        this.pollingInterval = pollingInterval;
    }

    /**
     * Toute les 90 secondes, la sonde doit s'annoncer en multicast pour ensuite récupérer et mettre à jour les configs.
     */
    public abstract void start();

    /**
     * La sonde doit se désinscrire du multicast et le programme doit se terminer.
     */
    public abstract void stop();

    /**
     * En fonction de la config, la sondes doit collecter des données à intervalles réguliers.
     */
    protected abstract void collectData();
}