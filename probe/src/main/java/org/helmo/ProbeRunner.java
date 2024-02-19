package org.helmo;

public class ProbeRunner {
    final static String CONFIG_PROBES = "json/src/main/resources/config-probes.json";
    final static String CONFIG_MONITOR = "json/src/main/resources/config-monitor.json";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Veuillez spécifier le type de sonde à exécuter ('https' ou 'snmp').");
            System.exit(1);
        }

        String probeType = args[0].toLowerCase();
        JsonReader jsonReader = new JsonReader();
        ConfigProbes configProbes = jsonReader.readConfigProbe(CONFIG_PROBES);
        ConfigMonitor configMonitor = jsonReader.readConfigMonitor(CONFIG_MONITOR);
        switch (probeType) {
            case "https":
                runHttpsProbe(configProbes, configMonitor);
                break;
            case "snmp":
                runSnmpProbe(configProbes, configMonitor);
                break;
            case "imap":
                runImapProbe(configProbes, configMonitor);
                break;
            default:
                System.out.println("Type de sonde non reconnu. Les options valides sont 'https' ou 'snmp'.");
                System.exit(1);
        }
    }

    private static void runImapProbe(ConfigProbes configProbes, ConfigMonitor configMonitor) {
        Probe probe = new ProbeIMAP(configMonitor, configProbes);
        probe.start();
    }


    private static void runHttpsProbe(ConfigProbes configProbes, ConfigMonitor configMonitor) {
        Probe probe = new ProbeHttps(configMonitor, configProbes);
        probe.start();
    }

    private static void runSnmpProbe(ConfigProbes configProbes, ConfigMonitor configMonitor) {
        Probe probe = new ProbeSNMP(configMonitor, configProbes);
        probe.start();
    }
}

