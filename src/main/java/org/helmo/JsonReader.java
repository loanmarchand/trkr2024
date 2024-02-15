package org.helmo;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.parser.ParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class JsonReader {

    public ConfigMonitor readConfigMonitor(String fileName) {

        try{
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(fileName));
            JSONObject jsonObject = (JSONObject) obj;

            String multicastAddress = (String) jsonObject.get("multicastAddress");
            long multicastPort = (long) jsonObject.get("multicastPort");
            String multicastInterface = (String) jsonObject.get("multicastInterface");
            long clientPort = (long) jsonObject.get("clientPort");
            boolean tls = (boolean) jsonObject.get("tls");
            String aesKey = (String) jsonObject.get("aesKey");

            Map<String, Integer> protocolsDelay = (Map<String, Integer>) jsonObject.get("protocolsDelay");

            // Récupérer les données de la liste "probes" en tant qu'objet JSON
            JSONObject probesJson = (JSONObject) jsonObject.get("probes");

            // Initialiser une liste pour stocker les objets Aurl
            List<Aurl> probes = new ArrayList<>();

            // Parcourir les entrées de l'objet JSON des "probes"
            for (Object entry : probesJson.entrySet()) {
                Map.Entry<String, String> probeEntry = (Map.Entry<String, String>) entry;
                String probeId = probeEntry.getKey();
                String probeValue = probeEntry.getValue();

                // Utiliser votre méthode pour parser une URL augmentée et l'ajouter à la liste
                Aurl aurl = URLParser.parseAugmentedUrl(probeValue);
                if (aurl != null) {
                    probes.add(aurl);
                }
            }

        }catch (IOException | ParseException e) {
            e.printStackTrace();
        }


        return null;
    }


}
