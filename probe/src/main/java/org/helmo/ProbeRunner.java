package org.helmo;

public class ProbeRunner {

    public static void main(String[] args) {
        runSnmpProbe();


    }

    private static void runHttpsProbe() {
        JsonReader jsonReader = new JsonReader();
        ConfigProbes configProbes = jsonReader.readConfigProbe("json/src/main/resources/config-probes.json");
        ConfigMonitor configMonitor = jsonReader.readConfigMonitor("json/src/main/resources/config-monitor.json");
        Probe probe = new ProbeHttps(configMonitor, configProbes);
        probe.start();
    }

    private static void runSnmpProbe() {
        JsonReader jsonReader = new JsonReader();
        ConfigProbes configProbes = jsonReader.readConfigProbe("json/src/main/resources/config-probes.json");
        ConfigMonitor configMonitor = jsonReader.readConfigMonitor("json/src/main/resources/config-monitor.json");

        Probe probe = new ProbeSNMP(configMonitor, configProbes);

        // Démarrer le probe
        probe.start();
    }
}

