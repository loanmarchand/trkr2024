import org.helmo.*;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class JsonHelperTest {

    @Test
    void testReadConfigMonitor() { //TODO : changer le path et renommer les tests
        JsonHelper jsonHelper = new JsonHelper();
        ConfigMonitor configMonitor = jsonHelper.readConfigMonitor("../json/src/test/resources/config-monitor.json");
        assertNotNull(configMonitor);
        assertEquals("224.0.0.254", configMonitor.multicastAdress());
        assertEquals(65001, configMonitor.multicastPort());
        assertEquals("monInterfaceReseau_12345", configMonitor.multicastInterface());
        assertEquals(12345, configMonitor.clientPort());
        assertFalse(configMonitor.tls());
        assertEquals("MaCleAESGenereeParMesSoins", configMonitor.aesKey());
        assertNotNull(configMonitor.protocolsDelay());
        assertEquals("120", configMonitor.protocolsDelay().get("snmp"));
        assertEquals("120", configMonitor.protocolsDelay().get("https"));
        assertNotNull(configMonitor.probes());
        assertEquals(6, configMonitor.probes().size());

        // Vérifiez les détails de chaque sonde

        //"http1": "http1!https://www.swilabus.com/!0!1500",
        assertTrue(configMonitor.probes().contains(new Aurl("http1", new Url("https", null, null, "www.swilabus.com", -1, null), 0, 1500)));

        //"http2": "http2!https://www.swilabus.be/!0!2000",
        assertTrue(configMonitor.probes().contains(new Aurl("http2", new Url("https", null, null, "www.swilabus.be", -1, null), 0, 2000)));

        //"http3": "http3!https://www.swilabus.com/trkr1!0!1700",
        assertTrue(configMonitor.probes().contains(new Aurl("http3", new Url("https", null, null, "www.swilabus.com", -1, "/trkr1"), 0, 1700)));

        //"http4": "http4!https://www.swilabus.com/trkr2!0!1800"
        assertTrue(configMonitor.probes().contains(new Aurl("http4", new Url("https", null, null, "www.swilabus.com", -1, "/trkr2"), 0, 1800)));

        //"snmp1": "snmp1!snmp://superswila:TeamG0D$wila#iLikeGodSWILA2024@v3.swi.la:6161/1.3.6.1.4.1.2021.4.11.0!10000!99999999",
        assertTrue(configMonitor.probes().contains(new Aurl("snmp1", new Url("snmp", "superswila", "TeamG0D$wila#iLikeGodSWILA2024", "v3.swi.la", 6161, "/1.3.6.1.4.1.2021.4.11.0"), 10000, 99999999)));

        //"snmp2": "snmp2!snmp://1amMemb3r0fTe4mSWILA@trkr.swilabus.com:161/1.3.6.1.4.1.2021.11.11.0!10!99999999"
        assertTrue(configMonitor.probes().contains(new Aurl("snmp2", new Url("snmp", "1amMemb3r0fTe4mSWILA", null, "trkr.swilabus.com", 161, "/1.3.6.1.4.1.2021.11.11.0"), 10, 99999999)));
    }

    @Test
    void testReadConfigProbe() {
        JsonHelper jsonHelper = new JsonHelper();
        ConfigProbes configProbes = jsonHelper.readConfigProbe("../json/src/test/resources/config-probes-snmp.json");
        assertNotNull(configProbes);
        assertEquals("snmp", configProbes.protocol());
        assertEquals("224.0.0.254", configProbes.multicastAddress());
        assertEquals(65001, configProbes.multicastPort());
        assertEquals("monInterfaceReseau_12345", configProbes.multicastInterface());
        assertEquals(10, configProbes.multicastDelay());
        assertEquals(65002, configProbes.unicastPort());
        assertEquals("MaCleAESGenereeParMesSoins", configProbes.aesKey());

    }

    // Test d'ajout d'une seule probe
    @Test
    public void testAddProbe() throws IOException {
        // Chemin vers le fichier JSON de test
        String fileName = "/Users/jillianrezette/Library/Mobile Documents/com~apple~CloudDocs/Helmo - Bac 2/Projet/trkr2024/json/src/test/resources/config-monitor.json";

        // Création d'une instance de JsonHelper
        JsonHelper jsonHelper = new JsonHelper();

        // Ajout d'une nouvelle probe
        jsonHelper.addProbe(fileName, "https5", "http5!https://www.example.com/!0!2500");

        // Vérification que la nouvelle probe a été ajoutée avec succès
        ConfigMonitor configMonitor = jsonHelper.readConfigMonitor(fileName);
        assertNotNull(configMonitor);
        assertEquals(jsonHelper.countProbes(fileName), configMonitor.probes().size()); // Vérifie qu'il y a 6 probes après l'ajout
        assertTrue(configMonitor.probes().stream().anyMatch(p -> p.url().host().equals("www.example.com"))); // Vérifie que la nouvelle probe a bien été ajoutée
    }
}
