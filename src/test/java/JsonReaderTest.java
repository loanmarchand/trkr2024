import org.helmo.ConfigMonitor;
import org.helmo.JsonReader;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JsonReaderTest {

    @Test
    void testReadConfigMonitor() {
        JsonReader jsonReader = new JsonReader();
        ConfigMonitor configMonitor = jsonReader.readConfigMonitor("/Users/jillianrezette/Library/Mobile Documents/com~apple~CloudDocs/Helmo - Bac 2/Projet/trkr2024/ressources/config-monitor.json");

        assertNotNull(configMonitor);
        assertEquals("224.0.0.254", configMonitor.multicastAdress());
        assertEquals(65001, configMonitor.multicastPort());
        assertEquals("monInterfaceReseau_12345", configMonitor.multicastInterface());
        assertEquals(12345, configMonitor.clientPort());
        assertFalse(configMonitor.tls());
        assertEquals("MaCleAESGenereeParMesSoins", configMonitor.aesKey());
        assertNotNull(configMonitor.protocolsDelay());
        assertEquals(120, configMonitor.protocolsDelay().get("snmp"));
        assertEquals(120, configMonitor.protocolsDelay().get("https"));
        assertNotNull(configMonitor.probes());
        assertEquals(6, configMonitor.probes().size());

        // Vérifiez d'autres propriétés de ConfigMonitor en fonction de votre implémentation
    }
}
