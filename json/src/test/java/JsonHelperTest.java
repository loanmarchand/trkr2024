import org.helmo.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JsonHelperTest {

    @Test
    void testReadConfigMonitor() { //TODO : changer le path et renommer les tests
        JsonHelper jsonHelper = new JsonHelper();
        ConfigMonitor configMonitor = jsonHelper.readConfigMonitor("../json/src/test/resources/config-monitor.json");
        assertNotNull(configMonitor);
        assertEquals("224.0.0.254", configMonitor.multicastAdress());
        assertEquals(65001, configMonitor.multicastPort());
        assertEquals("enxf8e43bbeaf49", configMonitor.multicastInterface());
        assertEquals(12345, configMonitor.clientPort());
        assertFalse(configMonitor.tls());
        assertEquals("853QlfQasa2OJQlokSYPUZwzhH25sWmcvBkV1yD0Q1yDbf4uB/SHVVdGphA7V2nA4iE78EUikhj95iltTO98gKj3ueX/KxXRkkXtUL+Vk8Ep+xghJk4Ydtm+VDmkQfwVY/3OPLa4HjkGZOZ8Bge+Bg16tdpiUxTwYD+g62NgLpSODP1m/zcOVA8WJ9eUxL+gSrmCtnlpeEc6OQoI8rDOBHgBlfFSk0E+dE0iX2m/HDauUdG7Q4KAD16pH5Wfrgte4ZF449twZMZzm2Hg9JmVPuR0cuEw0qkeeclwN+ZDPN6aVy0s2uwG0RgpNn2rjCKNrNzMcALWckzpAp9plU8TcQ==", configMonitor.aesKey());
        assertNotNull(configMonitor.protocolsDelay());
        assertEquals("120", configMonitor.protocolsDelay().get("snmp"));
        assertEquals("10", configMonitor.protocolsDelay().get("https"));
        assertNotNull(configMonitor.probes());
        assertEquals(8, configMonitor.probes().size());

        // Vérifiez les détails de chaque sonde

        //"http1": "http1!https://www.swilabus.com/!0!1500",
        assertTrue(configMonitor.probes().contains(new Aurl("http1", new Url("https", null, null, "www.swilabus.com", -1, "/"), 0, 1500)));

        //"http2": "http2!https://www.swilabus.be/!0!2000",
        assertTrue(configMonitor.probes().contains(new Aurl("http2", new Url("https", null, null, "www.swilabus.be", -1, "/"), 0, 2000)));

        //"http3": "http3!https://www.swilabus.com/trkr1!0!1700",
        assertTrue(configMonitor.probes().contains(new Aurl("http3", new Url("https", null, null, "www.swilabus.com", -1, "/trkr1"), 0, 1700)));

        //"http4": "http4!https://www.swilabus.com/trkr2!0!1800"
        assertTrue(configMonitor.probes().contains(new Aurl("http4", new Url("https", null, null, "www.swilabus.com", -1, "/trkr2"), 0, 1800)));

        //"snmp1": "snmp1!snmp://superswila:TeamG0D$wila#iLikeGodSWILA2024@v3.swi.la:6161/1.3.6.1.4.1.2021.4.11.0!10000!99999999",
        assertTrue(configMonitor.probes().contains(new Aurl("snmp1", new Url("snmp", "superswila", "TeamG0D$wila#iLikeGodSWILA2024", "v3.swi.la", 6161, "/1.3.6.1.4.1.2021.4.11.0"), 10000, 99999999)));

        //"snmp2": "snmp2!snmp://1amMemb3r0fTe4mSWILA@trkr.swilabus.com:161/1.3.6.1.4.1.2021.11.11.0!10!99999999"
        assertTrue(configMonitor.probes().contains(new Aurl("snmp2", new Url("snmp", "1amMemb3r0fTe4mSWILA", null, "trkr.swilabus.com", 161, "/1.3.6.1.4.1.2021.11.11.0"), 10, 99999999)));
        //"imap2": "imap2!imap://loan@eyJpbnN0YWxsZWQiOnsiY2xpZW50X2lkIjoiMjc3NzU1OTIxMTc4LXA2M3M0bnQ0MmExaWkzN3BrMjgwb2Zodm9qaXM3MW4wLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwicHJvamVjdF9pZCI6InRya3IyMDI0IiwiYXV0aF91cmkiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20vby9vYXV0aDIvYXV0aCIsInRva2VuX3VyaSI6Imh0dHBzOi8vb2F1dGgyLmdvb2dsZWFwaXMuY29tL3Rva2VuIiwiYXV0aF9wcm92aWRlcl94NTA5X2NlcnRfdXJsIjoiaHR0cHM6Ly93d3cuZ29vZ2xlYXBpcy5jb20vb2F1dGgyL3YxL2NlcnRzIiwiY2xpZW50X3NlY3JldCI6IkdPQ1NQWC10VHU0eFhDcnBZTUl2R1IwMzdfQk43OVU0UlFvIiwicmVkaXJlY3RfdXJpcyI6WyJodHRwOi8vbG9jYWxob3N0Il19fQ==/!0!10"
        assertTrue(configMonitor.probes().contains(new Aurl("imap2", new Url("imap", "loan", null, "eyJpbnN0YWxsZWQiOnsiY2xpZW50X2lkIjoiMjc3NzU1OTIxMTc4LXA2M3M0bnQ0MmExaWkzN3BrMjgwb2Zodm9qaXM3MW4wLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwicHJvamVjdF9pZCI6InRya3IyMDI0IiwiYXV0aF91cmkiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20vby9vYXV0aDIvYXV0aCIsInRva2VuX3VyaSI6Imh0dHBzOi8vb2F1dGgyLmdvb2dsZWFwaXMuY29tL3Rva2VuIiwiYXV0aF9wcm92aWRlcl94NTA5X2NlcnRfdXJsIjoiaHR0cHM6Ly93d3cuZ29vZ2xlYXBpcy5jb20vb2F1dGgyL3YxL2NlcnRzIiwiY2xpZW50X3NlY3JldCI6IkdPQ1NQWC10VHU0eFhDcnBZTUl2R1IwMzdfQk43OVU0UlFvIiwicmVkaXJlY3RfdXJpcyI6WyJodHRwOi8vbG9jYWxob3N0Il19fQ==", -1, "/"), 0, 10)));
        //"imap1": "imap1!imap://trkr@eyJpbnN0YWxsZWQiOnsiY2xpZW50X2lkIjoiMjc3NzU1OTIxMTc4LXA2M3M0bnQ0MmExaWkzN3BrMjgwb2Zodm9qaXM3MW4wLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwicHJvamVjdF9pZCI6InRya3IyMDI0IiwiYXV0aF91cmkiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20vby9vYXV0aDIvYXV0aCIsInRva2VuX3VyaSI6Imh0dHBzOi8vb2F1dGgyLmdvb2dsZWFwaXMuY29tL3Rva2VuIiwiYXV0aF9wcm92aWRlcl94NTA5X2NlcnRfdXJsIjoiaHR0cHM6Ly93d3cuZ29vZ2xlYXBpcy5jb20vb2F1dGgyL3YxL2NlcnRzIiwiY2xpZW50X3NlY3JldCI6IkdPQ1NQWC10VHU0eFhDcnBZTUl2R1IwMzdfQk43OVU0UlFvIiwicmVkaXJlY3RfdXJpcyI6WyJodHRwOi8vbG9jYWxob3N0Il19fQ==/!0!10"
        assertTrue(configMonitor.probes().contains(new Aurl("imap1", new Url("imap", "trkr", null, "eyJpbnN0YWxsZWQiOnsiY2xpZW50X2lkIjoiMjc3NzU1OTIxMTc4LXA2M3M0bnQ0MmExaWkzN3BrMjgwb2Zodm9qaXM3MW4wLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwicHJvamVjdF9pZCI6InRya3IyMDI0IiwiYXV0aF91cmkiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20vby9vYXV0aDIvYXV0aCIsInRva2VuX3VyaSI6Imh0dHBzOi8vb2F1dGgyLmdvb2dsZWFwaXMuY29tL3Rva2VuIiwiYXV0aF9wcm92aWRlcl94NTA5X2NlcnRfdXJsIjoiaHR0cHM6Ly93d3cuZ29vZ2xlYXBpcy5jb20vb2F1dGgyL3YxL2NlcnRzIiwiY2xpZW50X3NlY3JldCI6IkdPQ1NQWC10VHU0eFhDcnBZTUl2R1IwMzdfQk43OVU0UlFvIiwicmVkaXJlY3RfdXJpcyI6WyJodHRwOi8vbG9jYWxob3N0Il19fQ==", -1, "/"), 0, 10)));

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
}
