package org.helmo;

public abstract class ProbeAsbtract {
    protected ConfigProbes configProbes;

    public ProbeAsbtract(ConfigProbes configProbes) {
        this.configProbes = configProbes;
    }

    /**
     * Toute les 90 secondes, la sonde doit s'annoncer en multicast pour ensuite récupérer et mettre à jour les configs.
     */
    public abstract void start();

    /**
     * La sonde doit se désinscrire du multicast et le programme doit se terminer.
     */
    public abstract void stop();


    public ConfigProbes getConfigProbes() {
        return configProbes;
    }

    public abstract void startThreadLoop(Runnable runnable, long delay);

    public abstract void sendMulticastMessage(String message);
}