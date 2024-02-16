/*
package org.helmo;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProbeSNMP extends Probe {
    private final int pollingInterval;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


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

        // TODO: il faudra récupérer les différentes informations de l'URL via les REGEX
        try {
            // Préparation de l'adresse et du transport
            Address targetAddress = GenericAddress.parse("udp:trkr.swilabus.com/161"); // TODO: ici
            TransportMapping<UdpAddress> transport = new DefaultUdpTransportMapping();
            transport.listen();

            // Création de la cible
            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString("1amMemb3r0fTe4mSWILA")); // TODO: ici
            target.setAddress(targetAddress);
            target.setRetries(2);
            target.setTimeout(1500);
            target.setVersion(SnmpConstants.version2c); // TODO: ici

            // Création du PDU
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.2021.11.11.0"))); // TODO: ici
            pdu.setType(PDU.GET);

            // Envoi de la requête SNMP
            Snmp snmp = new Snmp(transport);
            ResponseEvent response = snmp.get(pdu, target);

            // Vérification et affichage de la réponse
            if (response != null && response.getResponse() != null) {
                System.out.println("Réponse SNMP reçue : " + response.getResponse().get(0).getVariable().toString());
            } else {
                System.out.println("Aucune réponse ou erreur lors de la requête SNMP.");
            }

            snmp.close();
        } catch (Exception e) {
            e.printStackTrace(); // TODO: remplacer la gestion de l'erreur
            System.out.println("Erreur lors de la collecte des données SNMP.");
        }
    }
}
*/
