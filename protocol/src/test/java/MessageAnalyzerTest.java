import org.helmo.*;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class MessageAnalyzerTest {

    @Test
    void NewmonTest() {
        Command testing = MessageAnalyzer.analyzeMessage("NEWMON monid!https://salute.sal/ezajo!57575!54645654\r\n");
        assert testing != null;
        assertEquals("NEWMON", testing.getCommandType());
        assertEquals(testing.getAurl().type(),"monid");
        assertEquals(testing.getAurl().max(),54645654);
    }
    @Test
    void NewmonrespTest() {
        Command testing = MessageAnalyzer.analyzeMessage("+OK salute\r\n");
        assert testing != null;
        assertEquals("NEWMON_RESP", testing.getCommandType());
        assertEquals("salute", testing.getNewmonrespMessage());
        assertEquals("+OK",testing.getStatusNewmonresp());
    }
    @Test
    void ListmonTest() {
        Command testing = MessageAnalyzer.analyzeMessage("LISTMON\r\n");
        assert testing != null;
        assertEquals("LISTMON", testing.getCommandType());
    }
    @Test
    void MonTest() {
        Command testing = MessageAnalyzer.analyzeMessage("MON myid1 myid2\r\n");
        assert testing != null;
        assertEquals("MON", testing.getCommandType());
        assertEquals("myid1", testing.getIdList().get(0));
        assertEquals("myid2", testing.getIdList().get(1));
        assertEquals(2, testing.getIdList().size());
    }
    @Test
    void RequestTest() {
        Command testing = MessageAnalyzer.analyzeMessage("REQUEST myid1\r\n");
        assert testing != null;
        assertEquals("REQUEST", testing.getCommandType());
        assertEquals("myid1", testing.getId());
    }
    @Test
    void RespondTest() {
        Command testing = MessageAnalyzer.analyzeMessage("RESPOND myid1 https://myurl.be/ ALARM\r\n");
        assert testing != null;
        assertEquals("RESPOND", testing.getCommandType());
        assertEquals("myid1", testing.getId());
        assertEquals("https://myurl.be/", testing.getUrlEtPath());
        assertEquals("ALARM", testing.getState());
        testing = MessageAnalyzer.analyzeMessage("RESPOND myid1 https://username:password@myurl.be:25565/ ALARM\r\n");
        assert testing != null;
        assertEquals("https://username:password@myurl.be:25565/", testing.getUrlEtPath());
    }



    @Test
    void SetupTest() {
        Command testing = MessageAnalyzer.analyzeMessage("SETUP 112255 monid!https://salute.sal/ezajo!57575!54645654 monid2!https://salute.sal/ezajo!57575!666666\r\n");
        assert testing != null;
        System.out.println(testing.getCommandType());
        assertEquals("SETUP", testing.getCommandType());
        assertEquals("112255", testing.getFrequency());
        assertEquals("monid", testing.getAurlList().get(0).type());
        assertEquals(666666, testing.getAurlList().get(1).max());
        assertEquals(2, testing.getAurlList().size());
    }
    @Test
    void StatusofTest() {
        Command testing = MessageAnalyzer.analyzeMessage("STATUSOF myid1\r\n");
        assert testing != null;
        assertEquals("STATUSOF", testing.getCommandType());
        assertEquals(testing.getId(),"myid1");
    }
    @Test
    void StatusTest() {
        Command testing = MessageAnalyzer.analyzeMessage("STATUS myid1 ALARM\r\n");
        assert testing != null;
        assertEquals("STATUS", testing.getCommandType());
        assertEquals(testing.getId(),"myid1");
        assertEquals(testing.getState(),"ALARM");
    }



    @Test
    void ProbeTest() {
        Command testing = MessageAnalyzer.analyzeMessage("PROBE https 25565\r\n");
        assert testing != null;
        assertEquals("PROBE", testing.getCommandType());
        assertEquals(testing.getProtocole(),"https");
        assertEquals(testing.getPort(),"25565");
    }
    @Test
    void DataTest() {
        Command testing = MessageAnalyzer.analyzeMessage("DATA https 25565\r\n");
        assert testing != null;
        assertEquals("DATA", testing.getCommandType());
        assertEquals(testing.getProtocole(),"https");
        assertEquals(testing.getPort(),"25565");
    }


}