package org.helmo;

import org.helmo.probe.Probe;
import org.helmo.probe.ProbeSNMP;

public class Main {
    public static void main(String[] args) {
        // Probe de test
        String servicesURL = "1amMemb3r0fTe4mSWILA@trkr.swilabus.com:161/1.3.6.1.4.1.2021.11.11.0";
        Probe probe = new ProbeSNMP(servicesURL, 1000);

        // Démarrer le probe
        probe.start();
    }
}