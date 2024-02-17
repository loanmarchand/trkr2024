package org.helmo;

public class ProbeRunner {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Veuillez spécifier le type de sonde à exécuter ('https' ou 'snmp').");
            System.exit(1);
        }

        String probeType = args[0].toLowerCase();
        switch (probeType) {
            case "https":
                runHttpsProbe();
                break;
            case "snmp":
                runSnmpProbe();
                break;
            default:
                System.out.println("Type de sonde non reconnu. Les options valides sont 'https' ou 'snmp'.");
                System.exit(1);
        }
    }

    private static void runHttpsProbe() {
        // TODO: completer la méthode runHttpsProbe
        System.out.println("Probe HTTPS");
    }

    private static void runSnmpProbe() {
        System.out.println("Probe SNMP");

        // Probe de test
        String servicesURL1 = "1amMemb3r0fTe4mSWILA@trkr.swilabus.com:161/1.3.6.1.4.1.2021.11.11.0";
        String servicesURL2 = "superswila:TeamG0D$wila#iLikeGodSWILA2024@v3.swi.la:6161/1.3.6.1.4.1.2021.4.11.0";

        Probe probe = new ProbeSNMP(servicesURL1, 1000);

        // Démarrer le probe
        probe.start();
    }
}

