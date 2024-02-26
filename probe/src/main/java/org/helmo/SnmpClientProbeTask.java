package org.helmo;

import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.fluent.SnmpBuilder;
import org.snmp4j.fluent.TargetBuilder;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class SnmpClientProbeTask extends AbstractProbeTask {


    public SnmpClientProbeTask(Socket socket, Probe probe) {
        super(socket, probe);
    }
    protected void collectData() {
        boolean hasChanged = false;
        for (Aurl value : aurlsStatus.keySet()) {
            if (value.type().contains("snmp")) {
                if (value.url().password() == null) {
                    hasChanged = collectDataV2(value);
                } else {
                    try {
                        hasChanged = collectDataV3(value);
                    } catch (IOException e) {
                        System.out.println("Erreur lors de la collecte des données SNMP: " + e.getMessage());
                    }
                }
            }
        }
        if (hasChanged) {
            String message = MessageBuilder.buildData(probe.getConfigProbes().protocol(), probe.getConfigProbes().unicastPort());
            probe.sendMulticastMessage(message);
        }
    }

    private boolean collectDataV3(Aurl aurl) throws IOException {
        String target = String.format("udp:%s/%d", aurl.url().host(), aurl.url().port());
        SnmpBuilder snmpBuilder = new SnmpBuilder();
        Snmp snmp = snmpBuilder.udp().securityProtocols(SecurityProtocols.SecurityProtocolSet.maxCompatibility).v3().usm().threads(2).build();
        snmp.listen();
        Address targetAddress = GenericAddress.parse(target);
        byte[] targetEngineID = snmp.discoverAuthoritativeEngineID(targetAddress, 6000);
        if (targetEngineID != null) {
            TargetBuilder<?> targetBuilder = snmpBuilder.target(targetAddress);
            Target<?> userTarget = targetBuilder
                    .user(aurl.url().user(), targetEngineID)
                    .auth(TargetBuilder.AuthProtocol.sha1).authPassphrase(aurl.url().password().split("#")[1])
                    .priv(TargetBuilder.PrivProtocol.aes128).privPassphrase(aurl.url().password().split("#")[0])
                    .done()
                    .timeout(3 * 1000L).retries(1)
                    .build();
            userTarget.setSecurityLevel(SecurityLevel.AUTH_PRIV);

            PDU pdu = targetBuilder.pdu().type(PDU.GET).oids(aurl.url().path().substring(1)).contextName("").build();
            ResponseEvent<?> responseEventSnmp = snmp.get(pdu, userTarget);
            if (responseEventSnmp.getResponse() != null) {
                snmp.close();
                return handleResponse(responseEventSnmp, aurl);
            } else {
                System.err.println("Timeout on engine ID discovery for " + targetAddress + ", GET not sent.");
                snmp.close();
                return false;
            }
        } else {
            System.err.println("Timeout on engine ID discovery for " + targetAddress + ", GET not sent.");
            snmp.close();
            return false;

        }
    }

    private boolean collectDataV2(Aurl aurl) {
        try (TransportMapping<?> transport = new DefaultUdpTransportMapping()) {
            Snmp snmp = new Snmp(transport);
            transport.listen();
            Address targetAddress = GenericAddress.parse(String.format("udp:%s/%d", aurl.url().host(), aurl.url().port()));
            CommunityTarget<Address> target = new CommunityTarget<>();
            target.setCommunity(new OctetString(aurl.url().user()));
            target.setAddress(targetAddress);
            target.setVersion(SnmpConstants.version2c);
            return sendRequest(snmp, target, aurl.url().path().substring(1), aurl);
        } catch (IOException e) {
            System.out.println("Erreur lors de la collecte des données SNMP: " + e.getMessage());
            return false;
        }
    }

    private boolean sendRequest(Snmp snmp, CommunityTarget<Address> target, String oid, Aurl aurl) throws IOException {
        PDU pdu = new PDU(); // Utilisez PDU pour SNMPv1 et SNMPv2c

        pdu.add(new VariableBinding(new OID(oid)));
        pdu.setType(PDU.GET);

        ResponseEvent<Address> response = snmp.get(pdu, target);
        return handleResponse(response, aurl);

    }


    private boolean handleResponse(ResponseEvent<?> response, Aurl aurl) {
        String result = null;
        if (response != null && response.getResponse() != null) {
            for (VariableBinding vb : response.getResponse().getVariableBindings()) {
                //Si vb est infériéer a aurl.min alors on met le status a ALARM sinon OK
                if (vb.getVariable().toInt() < aurl.min()) {
                    result = "ALARM";
                } else {
                    result = "OK";
                }
            }
        } else {
            result = "DOWN";
        }
        if (!Objects.equals(aurlsStatus.get(aurl), result)) {
            aurlsStatus.put(aurl, result);
            return true;
        }
        return false;
    }
}
