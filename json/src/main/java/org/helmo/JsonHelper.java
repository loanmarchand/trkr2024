package org.helmo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class JsonHelper {

    public ConfigMonitor readConfigMonitor(String fileName) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(new File(fileName));

            String multicastAddress = rootNode.get("multicastAddress").asText();
            int multicastPort = rootNode.get("multicastPort").asInt();
            String multicastInterface = rootNode.get("multicastInterface").asText();
            int clientPort = rootNode.get("clientPort").asInt();
            boolean tls = rootNode.get("tls").asBoolean();
            String aesKey = rootNode.get("aesKey").asText();

            Map<String, String> protocolsDelay = getProtocolsDelay(rootNode);

            List<Aurl> probes = getAurls(rootNode);

            return new ConfigMonitor(multicastAddress, multicastPort, multicastInterface, clientPort, tls, aesKey, protocolsDelay, probes);
        } catch (IOException e) {
            e.printStackTrace(); //TODO: handle exception
        }

        return null;
    }

    private List<Aurl> getAurls(JsonNode rootNode) {
        JsonNode probesNode = rootNode.get("probes");

        List<Aurl> probes = new ArrayList<>();

        if (probesNode.isObject()) {
            probesNode.fields().forEachRemaining(entry -> {
                String probeId = entry.getKey();
                String probeValue = entry.getValue().asText();

                Aurl aurl = URLParser.parseAugmentedUrl(probeValue);
                if (aurl != null) {
                    probes.add(aurl);
                }
            });
        }
        return probes;
    }

    private Map<String, String> getProtocolsDelay(JsonNode rootNode) {
        Map<String, String> protocolsDelay = new HashMap<>();
        JsonNode protocolsDelayNode = rootNode.get("protocolsDelay");
        if (protocolsDelayNode.isObject()) {
            protocolsDelayNode.fields().forEachRemaining(entry -> {
                String protocolKey = entry.getKey();
                String protocolValue = entry.getValue().asText(); // Assume that value is convertible to String
                protocolsDelay.put(protocolKey, protocolValue);
            });
        }
        return protocolsDelay;
    }

    public ConfigProbes readConfigProbe(String fileName) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(new File(fileName));

            String protocol = rootNode.get("protocol").asText();
            String multicastAddress = rootNode.get("multicastAddress").asText();
            int multicastPort = rootNode.get("multicastPort").asInt();
            String multicastInterface = rootNode.get("multicastInterface").asText();
            int multicastDelay = rootNode.get("multicastDelay").asInt();
            int unicastPort = rootNode.get("unicastPort").asInt();
            String aesKey = rootNode.get("aesKey").asText();

            return new ConfigProbes(protocol, multicastAddress, multicastPort, multicastInterface, multicastDelay, unicastPort, aesKey);
        } catch (IOException e) {
            e.printStackTrace(); //TODO: handle exception
        }
        return null;
    }

}
