import org.helmo.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MessageAnalyzerTest {

    @Test
    void NewmonTest() {
        Command testing = MessageAnalyzer.analyzeMessage("NEWMON monid!https://salute.sal/ezajo!57575!54645654\r\n");
        assertEquals("NEWMON", testing.getCommandType());
        assertEquals("monid!https://salute.sal/ezajo!57575!54645654", testing.getAurl());
    }
    @Test
    void NewmonrespTest() {
        Command testing = MessageAnalyzer.analyzeMessage("+OK salute\r\n");
        assertEquals("NEWMON_RESP", testing.getCommandType());
        assertEquals("salute", testing.getNewmonrespMessage());
    }
    @Test
    void ListmonTest() {
        Command testing = MessageAnalyzer.analyzeMessage("LISTMON\r\n");
        assertEquals("LISTMON", testing.getCommandType());
    }
    @Test
    void MonTest() {
        Command testing = MessageAnalyzer.analyzeMessage("MON myid1 myid2\r\n");
        assertEquals("MON", testing.getCommandType());
        assertEquals("myid1", testing.getIdList().get(0));
        assertEquals("myid2", testing.getIdList().get(1));
        assertEquals(2, testing.getIdList().size());
    }
    @Test
    void RequestTest() {
        Command testing = MessageAnalyzer.analyzeMessage("REQUEST myid1\r\n");
        assertEquals("REQUEST", testing.getCommandType());
        assertEquals("myid1", testing.getId());
    }
    @Test
    void RespondTest() {
        Command testing = MessageAnalyzer.analyzeMessage("RESPOND myid1 https://myurl.be/ ALARM\r\n");
        assertEquals("RESPOND", testing.getCommandType());
        assertEquals("myid1", testing.getId());
        assertEquals("https://myurl.be/", testing.getUrlEtPath());
        assertEquals("ALARM", testing.getState());
        testing = MessageAnalyzer.analyzeMessage("RESPOND myid1 https://username:password@myurl.be:25565/ ALARM\r\n");
        assertEquals("https://username:password@myurl.be:25565/", testing.getUrlEtPath());
    }



    @Test
    void SetupTest() {
        Command testing = MessageAnalyzer.analyzeMessage("SETUP 112255 monid!https://salute.sal/ezajo!57575!54645654 monid2!https://salute.sal/ezajo!57575!54645654\r\n");
        System.out.println(testing.getCommandType());
        assertEquals("SETUP", testing.getCommandType());
        assertEquals("112255", testing.getFrequency());
        assertEquals("monid!https://salute.sal/ezajo!57575!54645654", testing.getAurlList().get(0));
        assertEquals("monid2!https://salute.sal/ezajo!57575!54645654", testing.getAurlList().get(1));
        assertEquals(2, testing.getAurlList().size());
    }
    @Test
    void StatusofTest() {
        Command testing = MessageAnalyzer.analyzeMessage("STATUSOF myid1\r\n");
        assertTrue(testing.getCommandType().equals("STATUSOF"));
        assertEquals(testing.getId(),"myid1");
    }
    @Test
    void StatusTest() {
        Command testing = MessageAnalyzer.analyzeMessage("STATUS myid1 ALARM\r\n");
        assertTrue(testing.getCommandType().equals("STATUS"));
        assertEquals(testing.getId(),"myid1");
        assertEquals(testing.getState(),"ALARM");
    }



    @Test
    void ProbeTest() {
        Command testing = MessageAnalyzer.analyzeMessage("PROBE https 25565\r\n");
        assertTrue(testing.getCommandType().equals("PROBE"));
        assertEquals(testing.getProtocole(),"https");
        assertEquals(testing.getPort(),"25565");
    }
    @Test
    void DataTest() {
        Command testing = MessageAnalyzer.analyzeMessage("DATA https 25565\r\n");
        assertTrue(testing.getCommandType().equals("DATA"));
        assertEquals(testing.getProtocole(),"https");
        assertEquals(testing.getPort(),"25565");
    }


}