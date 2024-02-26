import org.example.Aurl;
import org.example.URLParser;
import org.example.Url;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class URLParserTest { //TODO : renommer les tests

    @Test
    void testHttp1Probe() {
        //Aurl(String type, Url url, int min, int max)
        //Url(String protocol, String user, String password, String host, int port, String path)
        String probe = "http1!https://www.swilabus.com/!0!1500";
        Aurl aurl = URLParser.parseAugmentedUrl(probe);
        assertNotNull(aurl);
        assertEquals("http1", aurl.type());


        Url url = aurl.url();
        assertNotNull(url);
        assertEquals("https", url.protocol());
        assertEquals(null, url.user());
        assertEquals(null, url.password());
        assertEquals("www.swilabus.com", url.host());
        assertEquals(-1, url.port());
        assertEquals(null, url.path());


        assertEquals(0, aurl.min());
        assertEquals(1500, aurl.max());
    }

    @Test
    void testHttp2Probe() {
        String probe = "http2!https://www.swilabus.be/!0!2000";
        Aurl aurl = URLParser.parseAugmentedUrl(probe);
        assertNotNull(aurl);
        assertEquals("http2", aurl.type());

        Url url = aurl.url();
        assertNotNull(url);
        assertEquals("https", url.protocol());
        assertEquals(null, url.user());
        assertEquals(null, url.password());
        assertEquals("www.swilabus.be", url.host());
        assertEquals(-1, url.port());
        assertEquals(null, url.path());

        assertEquals(0, aurl.min());
        assertEquals(2000, aurl.max());
    }

    @Test
    void testHttp3Probe() {
        String probe = "http3!https://www.swilabus.com/trkr1!0!1700";
        Aurl aurl = URLParser.parseAugmentedUrl(probe);
        assertNotNull(aurl);
        assertEquals("http3", aurl.type());

        Url url = aurl.url();
        assertNotNull(url);
        assertEquals("https", url.protocol());
        assertEquals(null, url.user());
        assertEquals(null, url.password());
        assertEquals("www.swilabus.com", url.host());
        assertEquals(-1, url.port());
        assertEquals("/trkr1", url.path());

        assertEquals(0, aurl.min());
        assertEquals(1700, aurl.max());
    }

    @Test
    void testHttp4Probe() {
        String probe = "http4!https://www.swilabus.com/trkr2!0!1800";
        Aurl aurl = URLParser.parseAugmentedUrl(probe);
        assertNotNull(aurl);
        assertEquals("http4", aurl.type());

        Url url = aurl.url();
        assertNotNull(url);
        assertEquals("https", url.protocol());
        assertEquals(null, url.user());
        assertEquals(null, url.password());
        assertEquals("www.swilabus.com", url.host());
        assertEquals(-1, url.port());
        assertEquals("/trkr2", url.path());

        assertEquals(0, aurl.min());
        assertEquals(1800, aurl.max());
    }

    @Test
    void testSnmp1Probe() {
        String probe = "snmp1!snmp://superswila:TeamG0D$wila#iLikeGodSWILA2024@v3.swi.la:6161/1.3.6.1.4.1.2021.4.11.0!10000!99999999";
        Aurl aurl = URLParser.parseAugmentedUrl(probe);
        assertNotNull(aurl);
        assertEquals("snmp1", aurl.type());

        Url url = aurl.url();
        assertNotNull(url);
        assertEquals("snmp", url.protocol());
        assertEquals("superswila", url.user());
        assertEquals("TeamG0D$wila#iLikeGodSWILA2024", url.password());
        assertEquals("v3.swi.la", url.host());
        assertEquals(6161, url.port());
        assertEquals("/1.3.6.1.4.1.2021.4.11.0", url.path());

        assertEquals(10000, aurl.min());
        assertEquals(99999999, aurl.max());
    }

    @Test
    void testSnmp2Probe() {
        String probe = "snmp2!snmp://1amMemb3r0fTe4mSWILA@trkr.swilabus.com:161/1.3.6.1.4.1.2021.11.11.0!10!99999999";

        Aurl aurl = URLParser.parseAugmentedUrl(probe);
        assertNotNull(aurl);
        assertEquals("snmp2", aurl.type());

        Url url = aurl.url();
        assertNotNull(url);
        assertEquals("snmp", url.protocol());
        assertEquals("1amMemb3r0fTe4mSWILA", url.user());
        assertEquals(null, url.password());
        assertEquals("trkr.swilabus.com", url.host());
        assertEquals(161, url.port());
        assertEquals("/1.3.6.1.4.1.2021.11.11.0", url.path());

        assertEquals(10, aurl.min());
        assertEquals(99999999, aurl.max());
    }

}
