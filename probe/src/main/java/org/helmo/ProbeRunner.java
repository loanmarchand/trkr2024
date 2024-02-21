package org.helmo;

public class ProbeRunner {
    final static String CONFIG_PROBES_SNMP = "json/src/main/resources/config-probes-snmp.json";
    final static String CONFIG_PROBES_HTTPS = "json/src/main/resources/config-probes-http.json";
    final static String CONFIG_PROBES_IMAP = "json/src/main/resources/config-probes-imap.json";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Veuillez spécifier le type de sonde à exécuter ('https' ou 'snmp').");
            System.exit(1);
        }

        String probeType = args[0].toLowerCase();
        JsonReader jsonReader = new JsonReader();
        switch (probeType) {
            case "https":
                runHttpsProbe(jsonReader.readConfigProbe(CONFIG_PROBES_HTTPS));
                break;
            case "snmp":
                runSnmpProbe(jsonReader.readConfigProbe(CONFIG_PROBES_SNMP));
                break;
            case "imap":
                runImapProbe(jsonReader.readConfigProbe(CONFIG_PROBES_IMAP));
                break;
            default:
                System.out.println("Type de sonde non reconnu. Les options valides sont 'https' ou 'snmp'.");
                System.exit(1);
        }
    }

    private static void runImapProbe(ConfigProbes configProbes) {
        Probe probe = new ProbeIMAP(configProbes);
        probe.start();
    }


    private static void runHttpsProbe(ConfigProbes configProbes) {
        Probe probe = new ProbeHttps(configProbes);
        probe.start();
    }

    private static void runSnmpProbe(ConfigProbes configProbes) {
        Probe probe = new ProbeSNMP(configProbes);
        probe.start();
    }
}

