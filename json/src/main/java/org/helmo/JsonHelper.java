package org.helmo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
            System.out.println("Erreur lors de la lecture du fichier JSON : " + e.getMessage());
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
            System.out.println("Erreur lors de la lecture du fichier JSON : " + e.getMessage());
        }
        return null;
    }
/*
    public void addProbe(String fileName, String probeValue) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(fileName);
            JsonNode rootNode;

            // Vérifie si le fichier JSON existe déjà
            if (file.exists()) {
                rootNode = mapper.readTree(file);
            } else {
                // Si le fichier n'existe pas encore, crée un nouveau nœud racine
                rootNode = mapper.createObjectNode();
            }

            // Création d'un ObjectNode pour la nouvelle probe
            ObjectNode probeNode = mapper.createObjectNode();
            probeNode.put("probe", probeValue);

            // Récupération du nœud contenant les sondes
            JsonNode probesNode = rootNode.get("probes");

            // Vérification si le nœud des sondes existe et est un tableau
            if (probesNode == null || !probesNode.isArray()) {
                // Si le nœud des sondes n'existe pas ou n'est pas un tableau, crée un nouveau tableau
                probesNode = mapper.createArrayNode();
                ((ObjectNode) rootNode).set("probes", probesNode);
            }

            // Ajout de la nouvelle probe dans le tableau des sondes
            ((ArrayNode) probesNode).add(probeNode);

            // Écriture du JsonNode mis à jour dans le fichier JSON
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            writer.writeValue(file, rootNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */

    public void addProbe(String fileName, String probeKey, String probeValue) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(fileName);
            JsonNode rootNode;

            // Vérifie si le fichier JSON existe déjà
            if (file.exists()) {
                rootNode = mapper.readTree(file);
            } else {
                // Si le fichier n'existe pas encore, crée un nouveau nœud racine
                rootNode = mapper.createObjectNode();
            }

            // Création d'un ObjectNode pour la nouvelle sonde
            ObjectNode probeNode = mapper.createObjectNode();
            probeNode.put("probe", probeValue);

            // Récupération du nœud contenant les sondes
            ObjectNode probesNode = (ObjectNode) rootNode.get("probes");

            // Vérification si le nœud des sondes existe
            if (probesNode == null) {
                // Si le nœud des sondes n'existe pas, crée un nouveau nœud contenant les sondes
                probesNode = mapper.createObjectNode();
                ((ObjectNode) rootNode).set("probes", probesNode);
            }

            // Ajout de la nouvelle sonde avec la clé spécifiée
            probesNode.put(probeKey, probeValue);

            // Écriture du JsonNode mis à jour dans le fichier JSON
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            writer.writeValue(file, rootNode);
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture du fichier JSON : " + e.getMessage());
        }
    }


    public int countProbes(String fileName) {
        try {
            // Créer un ObjectMapper
            ObjectMapper mapper = new ObjectMapper();

            // Lire le fichier JSON
            JsonNode rootNode = mapper.readTree(new File(fileName));

            // Récupérer le nœud "probes"
            JsonNode probesNode = rootNode.get("probes");

            // Vérifier si le nœud "probes" existe et s'il s'agit d'un objet
            if (probesNode != null && probesNode.isObject()) {
                // Convertir le nœud "probes" en ObjectNode pour compter les éléments
                ObjectNode probesObject = (ObjectNode) probesNode;

                // Retourner le nombre d'éléments dans le nœud "probes"
                return probesObject.size();
            } else {
                // Si le nœud "probes" n'existe pas ou s'il n'est pas un objet, retourner 0
                return 0;
            }
        } catch (IOException e) {
            // Gérer les exceptions d'entrée/sortie
            System.out.println("Erreur lors de la lecture du fichier JSON : " + e.getMessage());
            return 0;
        }
    }
}
