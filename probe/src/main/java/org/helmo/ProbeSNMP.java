/*
package org.helmo;

import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.net.SocketException;
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
        System.out.println("Début de la collecte des données SNMP");
        TransportMapping<? extends Address> transport = null;
        try {
            transport = new DefaultUdpTransportMapping();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        Snmp snmp = new Snmp(transport);

        OctetString localEngineId = new OctetString(MPv3.createLocalEngineID());
        USM usm = new USM(SecurityProtocols.getInstance(), localEngineId, 0);
        SecurityModels.getInstance().addSecurityModel(usm);

// Remplacer par vos valeurs
        OctetString securityName = new OctetString("superswila");
        OID authProtocol = AuthSHA.ID; // Utilisation de SHA pour l'authentification
        OID privProtocol = PrivAES128.ID; // Supposition d'utilisation d'AES 128 pour le chiffrement
        OctetString authPassphrase = new OctetString("TeamG0D$wila");
        OctetString privPassphrase = new OctetString("iLikeGodSWILA2024");

        snmp.getUSM().addUser(securityName, new UsmUser(securityName, authProtocol, authPassphrase, privProtocol, privPassphrase));
        SecurityModels.getInstance().addSecurityModel(new TSM(localEngineId, false));

        UserTarget target = new UserTarget();
        target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
        target.setSecurityName(securityName);

// Remplacer "your-target-ip" et "your-port-number" par vos valeurs
        target.setAddress(GenericAddress.parse(String.format("udp:%s/%s", "v3.swi.la", "6161")));
        target.setVersion(SnmpConstants.version3);
        target.setRetries(2);
        target.setTimeout(60000);

        try {
            transport.listen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

// Pour un SET, vous devez avoir une OID et une valeur à définir. Si c'est juste un GET, changez le type et l'OID en conséquence.
        PDU pdu = new ScopedPDU();
        pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.2021.4.11.0"))); // Utilisez votre OID cible ici
        pdu.setType(PDU.GET); // Changez à PDU.GET si vous voulez juste récupérer la valeur
        ResponseEvent event = null;
        try {
            event = snmp.send(pdu, target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (event != null) {
            pdu = event.getResponse();
            if (pdu != null && pdu.getErrorStatus() == PDU.noError) {
                System.out.println("SNMPv3 operation Successful!");
            } else {
                System.out.println("SNMPv3 operation Unsuccessful. Error: " + (pdu != null ? pdu.getErrorStatusText() : "Response PDU is null"));
            }
        } else {
            System.out.println("SNMP send unsuccessful.");
        }

    }



}
*/
