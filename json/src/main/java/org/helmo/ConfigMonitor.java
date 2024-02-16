package org.helmo;

import java.util.List;
import java.util.Map;

public record ConfigMonitor(
        String multicastAdress, //"multicastAddress": "224.0.0.254",
        int multicastPort, // "multicastPort": 65001,
        String multicastInterface, // "multicastInterface": "monInterfaceReseau_12345",
        int clientPort, // "clientPort": 12345,
        boolean tls, // // "tls": false,
        String aesKey, //"aesKey": "MaCleAESGenereeParMesSoins",
        Map<String, String> protocolsDelay, // "protocolsDelay": { "snmp": 120, "https": 120 },
        List<Aurl> probes // "probes": {"http1": "http1!https://www.swilabus.com/!0!1500", ....
){
}