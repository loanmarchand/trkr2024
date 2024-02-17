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
    private final ScheduledExecutorService scheduler;
    private final ConfigMonitor configMonitor;
    private boolean running;

    public ProbeSNMP(ConfigMonitor configMonitor, ConfigProbes configProbes) {
        super(configProbes);
        this.configMonitor = configMonitor;
        this.scheduler = Executors.newScheduledThreadPool(3);
        running = false;
    }

    @Override
    public void start() {
        System.out.println("Start SNMP probe");
        running = true;
        startThreadLoop(this::collectData, Long.parseLong(configMonitor.protocolsDelay().get("snmp")));
    }

    @Override
    public void stop() {
        System.out.println("Stop SNMP probe");
    }

    @Override
    protected void collectData() {
        for (Aurl aurl : configMonitor.probes()) {
            if (aurl.type().contains("snmp")) {
                collectData(aurl);
            }
        }
    }

    private void collectData(Aurl aurl) {
        try {
            TransportMapping<?> transport = new DefaultUdpTransportMapping();
            Snmp snmp = new Snmp(transport);

            // Initialise SNMPv3
            USM usm = new USM(SecurityProtocols.getInstance().addDefaultProtocols(), new OctetString(MPv3.createLocalEngineID()), 0);
            SecurityModels.getInstance().addSecurityModel(usm);

            transport.listen();
            Target target = createTarget(snmp,aurl);
            sendRequest(snmp, target, aurl.url().path().substring(1));
            snmp.close();
        } catch (Exception e) {
            System.err.println("Erreur lors de la collecte des données SNMP: " + e.getMessage());
        }
    }

    private Target createTarget(Snmp snmp, Aurl aurl) {
        Address targetAddress = GenericAddress.parse(String.format("udp:%s/%d", aurl.url().host(), aurl.url().port()));
        if (aurl.url().password()!=null) { // SNMPv3
            UserTarget target = new UserTarget();
            target.setAddress(targetAddress);
            target.setVersion(SnmpConstants.version3);
            target.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
            target.setSecurityName(new OctetString(aurl.url().user()));
            OctetString userName = new OctetString(aurl.url().user());
            OctetString authPass = new OctetString(aurl.url().password());
            UsmUser user = new UsmUser(userName, AuthSHA.ID, authPass, null, null);
            snmp.getUSM().addUser(userName, user);

            return target;
        } else { // Par défaut, utilisez SNMPv2c
            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString(aurl.url().user()));
            target.setAddress(targetAddress);
            target.setVersion(SnmpConstants.version2c);
            return target;
        }
    }

    private void sendRequest(Snmp snmp, Target target, String oid) throws IOException {
        PDU pdu;
        if (target.getVersion() == SnmpConstants.version3) {
            pdu = new ScopedPDU(); // Utilisez ScopedPDU pour SNMPv3
        } else {
            pdu = new PDU(); // Utilisez PDU pour SNMPv1 et SNMPv2c
        }

        pdu.add(new VariableBinding(new OID(oid)));
        pdu.setType(PDU.GET);

        ResponseEvent response = snmp.get(pdu, target);
        handleResponse(response);
    }


    private void handleResponse(ResponseEvent response) {
        if (response != null && response.getResponse() != null) {
            for (VariableBinding vb : response.getResponse().getVariableBindings()) {
                System.out.println("Réponse SNMP: " + vb);
            }
        } else {
            System.out.println("Aucune réponse de l'hôte SNMP.");
        }
    }


    private void startThreadLoop(Runnable runnable, long delay) {
        scheduler.scheduleWithFixedDelay(() -> {
            if (running) {
                runnable.run();
            }
        }, 0, delay, TimeUnit.SECONDS);
    }



}
