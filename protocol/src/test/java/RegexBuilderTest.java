import org.helmo.Aurl;
import org.helmo.MessageBuilder;
import org.helmo.RegexBuilder;
import org.helmo.Url;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegexBuilderTest {
    @Test
    void UrlBuildTest() {
        Url url = new Url("https",null,null,"testing.com",-1, "/");
        assertEquals("https://testing.com/", RegexBuilder.buildUrl(url));
        url = new Url("https",null,null,"testing.com",25565, "/testeur");
        assertEquals("https://testing.com:25565/testeur", RegexBuilder.buildUrl(url));
        url = new Url("https","Cloan","password","testing.com",-1, "/");
        assertEquals("https://Cloan:password@testing.com/", RegexBuilder.buildUrl(url));
        url = new Url("https","Cloan","password","testing.com",25565, "/ououi");
        assertEquals("https://Cloan:password@testing.com:25565/ououi", RegexBuilder.buildUrl(url));
    }
    @Test
    void AurlBuildTest() {
        Url url = new Url("https","Cloan","password","testing.com",25565, "/ououi");
        Aurl aurl = new Aurl("myid1",url,22333,33444);
        assertEquals("myid1!https://Cloan:password@testing.com:25565/ououi!22333!33444", RegexBuilder.buildAurl(aurl));
    }
}
