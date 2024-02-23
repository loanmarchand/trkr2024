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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProbeSnmpRunable implements Runnable {

    private BufferedReader in;
    private PrintWriter out;
    private Probe probe;
    private final Map<Aurl, String> aurlsStatus;
    private int frequency;

    public ProbeSnmpRunable(Socket socket, Probe probe) {
        this.probe = probe;
        this.aurlsStatus = new HashMap<>();
        this.frequency = 0;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            System.out.println("Erreur lors de la création du BufferedReader et du PrintWriter: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("En attente de la configuration...");
            String configLine;
            configLine = in.readLine();
            System.out.println("Configuration reçue: " + configLine);
            Command command = MessageAnalyzer.analyzeMessage(configLine);
            if (command == null || Objects.equals(command.getCommandType(), "NONE")) {
                System.out.println("La configuration reçue est invalide.");
            } else if (Objects.equals(command.getCommandType(), "SETUP")) {
                System.out.println("La configuration reçue est valide.");
                List<Aurl> aurls = new ArrayList<>();
                command.getAurlList().forEach(aurl -> aurls.add(new Aurl("test", new Url("", "", "", "", 0, ""), 0, 0)));
                //TODO:  mettre les vrais valleurs vérifier que le type de aurl est égal a HTTPS
                aurls.forEach(aurl -> aurlsStatus.putIfAbsent(aurl, "UNKNOWN"));
                if (frequency == 0) {
                    frequency = Integer.parseInt(command.getFrequency());
                    probe.startThreadLoop(this::collectData, frequency);
                }
            }
        } catch (SocketTimeoutException e) {
            System.err.println("Aucune configuration reçue dans l'intervalle actuel.");
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture de la configuration: " + e.getMessage());
        }
    }


    private void collectData() {
        for (Aurl value : aurlsStatus.keySet()) {
            if (value.type().contains("snmp")) {
                if (value.url().password() == null) {
                    collectDataV2(value);
                } else {
                    try {
                        collectDataV3(value);
                    } catch (IOException e) {
                        System.out.println("Erreur lors de la collecte des données SNMP: " + e.getMessage());
                    }
                }
            }
        }
    }

    private void collectDataV3(Aurl aurl) throws IOException {
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
                StringBuilder result = new StringBuilder();
                List<VariableBinding> vbs = responseEventSnmp.getResponse().getAll();
                for (VariableBinding vb : vbs) {
                    result.append(vb.getVariable().toString());
                }
                snmp.close();
                System.out.println("Réponse SNMP: " + result);
            } else {
                System.err.println("Aucune réponse de l'hôte SNMP.");
            }
        } else {
            System.err.println("Timeout on engine ID discovery for " + targetAddress + ", GET not sent.");
            snmp.close();

        }
    }

    private void collectDataV2(Aurl aurl) {


        try (TransportMapping<?> transport = new DefaultUdpTransportMapping()) {
            Snmp snmp = new Snmp(transport);
            transport.listen();
            Address targetAddress = GenericAddress.parse(String.format("udp:%s/%d", aurl.url().host(), aurl.url().port()));
            CommunityTarget<Address> target = new CommunityTarget<>();
            target.setCommunity(new OctetString(aurl.url().user()));
            target.setAddress(targetAddress);
            target.setVersion(SnmpConstants.version2c);
            sendRequest(snmp, target, aurl.url().path().substring(1));
        } catch (IOException e) {
            System.out.println("Erreur lors de la collecte des données SNMP: " + e.getMessage());
        }
    }

    private void sendRequest(Snmp snmp, CommunityTarget<Address> target, String oid) throws IOException {
        PDU pdu = new PDU(); // Utilisez PDU pour SNMPv1 et SNMPv2c

        pdu.add(new VariableBinding(new OID(oid)));
        pdu.setType(PDU.GET);

        ResponseEvent<Address> response = snmp.get(pdu, target);
        handleResponse(response);

    }


    private void handleResponse(ResponseEvent<?> response) {
        if (response != null && response.getResponse() != null) {
            for (VariableBinding vb : response.getResponse().getVariableBindings()) {
                System.out.println("Réponse SNMP: " + vb);
            }
        } else {
            System.out.println("Aucune réponse de l'hôte SNMP.");
        }
    }



}
