import org.helmo.Aurl;
import org.helmo.RegexAnalyzer;
import org.helmo.Url;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RegexAnalyzerTest {

    @Test
    void CompleteUrlAnalyzerTest() {
        Url url = RegexAnalyzer.analyzeUrl("https://loanuser:passloan@swilabus.com:25565/salute");
        assert url != null;
        assertEquals(url.protocol(),"https");
        assertEquals(url.user(),"loanuser");
        assertEquals(url.password(),"passloan");
        assertEquals(url.host(),"swilabus.com");
        assertEquals(url.port(),25565);
        assertEquals(url.path(),"/salute");
    }
    @Test
    void UserPassUrlAnalyzerTest() {
        Url url = RegexAnalyzer.analyzeUrl("https://loanuser:passloan@swilabus.com/salute");
        assert url != null;
        assertEquals(url.protocol(),"https");
        assertEquals(url.user(),"loanuser");
        assertEquals(url.password(),"passloan");
        assertEquals(url.host(),"swilabus.com");
        assertEquals(url.port(),-1);
        assertEquals(url.path(),"/salute");
    }
    @Test
    void PortUrlAnalyzerTest() {
        Url url = RegexAnalyzer.analyzeUrl("https://swilabus.com:25565/salute");
        assert url != null;
        assertEquals(url.protocol(),"https");
        assertNull(url.user());
        assertNull(url.password());
        assertEquals(url.host(),"swilabus.com");
        assertEquals(url.port(),25565);
        assertEquals(url.path(),"/salute");
    }
    @Test
    void UrlAnalyzerTest() {
        Url url = RegexAnalyzer.analyzeUrl("https://swilabus.com/salute");
        assert url != null;
        assertEquals(url.protocol(),"https");
        assertNull(url.user());
        assertNull(url.password());
        assertEquals(url.host(),"swilabus.com");
        assertEquals(url.port(),-1);
        assertEquals(url.path(),"/salute");
    }

    @Test
    void AurlAnalyzerTest() {
        Aurl aurl = RegexAnalyzer.analyzeAurl("myid1!https://swilabus.com:25565/salute!2233!33345");
        assert aurl!= null;
        assertEquals(aurl.type(),"myid1");
        assertEquals(aurl.url().port(),25565);
        assertEquals(aurl.min(),2233);
        assertEquals(aurl.max(),33345);
    }

}
