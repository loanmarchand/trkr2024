package org.helmo;

import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProbeSNMP extends Probe {
    private final int pollingInterval;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private boolean isAurl = false;


    public ProbeSNMP(String servicesURL, int pollingInterval) {
        super(servicesURL, pollingInterval);
        this.pollingInterval = pollingInterval;
    }

    @Override
    public void start() {
        System.out.println("Start SNMP probe");
        scheduler.scheduleAtFixedRate(this::collectData, 0, pollingInterval, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        System.out.println("Stop SNMP probe");
    }

    @Override
    protected void collectData() {
        System.out.println("Collect data from SNMP probe");

        // TODO: remplacer les variables suivantes par les valeurs de l'URL vie les REGEX
        // Simule le résultat de la détection URL vs AURL
        boolean isAurl = false;

        // Variables pour SNMPv2c
        String addressV2c = "udp:trkr.swilabus.com/161";
        String community = "1amMemb3r0fTe4mSWILA";
        String oidV2c = "1.3.6.1.4.1.2021.11.11.0";

        try {
            // Initialisation de TransportMapping et Snmp
            TransportMapping<UdpAddress> transport = new DefaultUdpTransportMapping();
            transport.listen();
            Snmp snmp = new Snmp(transport);

            if (!isAurl) { // SNMPv2c
                CommunityTarget target = new CommunityTarget();
                target.setCommunity(new OctetString(community));
                target.setAddress(GenericAddress.parse(addressV2c));
                target.setRetries(2);
                target.setTimeout(1500);
                target.setVersion(SnmpConstants.version2c);

                PDU pdu = new PDU();
                pdu.add(new VariableBinding(new OID(oidV2c)));
                pdu.setType(PDU.GET);

                ResponseEvent response = snmp.get(pdu, target);

                if (response != null && response.getResponse() != null) {
                    System.out.println("Réponse SNMP reçue : " + response.getResponse().get(0).getVariable().toString());
                } else {
                    System.out.println("Aucune réponse ou erreur lors de la requête SNMP.");
                }
            } else { // SNMPv3

            }

            snmp.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la collecte des données SNMP.");
        }
    }

}
