import org.helmo.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MessageBuilderTest {

    @Test
    void NewmonBuildTest() {
        Url url = new Url("https",null,null,"salute.sal",-1, "/ezajo");
        Aurl aurl = new Aurl("monid",url,57575,54645654);
        assertEquals("NEWMON monid!https://salute.sal/ezajo!57575!54645654\r\n", MessageBuilder.buildNewmon(aurl));
    }
    @Test
    void NewmonrespBuildTest() {
        assertEquals("+OK\r\n", MessageBuilder.buildNewmonResp("+OK"));
        assertEquals("-ERR\r\n", MessageBuilder.buildNewmonResp("-ERR"));
        assertEquals("+OK Tout es ok\r\n", MessageBuilder.buildNewmonResp("+OK", "Tout es ok"));
    }
    @Test
    void ListmonBuildTest() {
        assertEquals("LISTMON\r\n", MessageBuilder.buildListmon());
    }
    @Test
    void MonBuildTest() {
        List<String> idList = new ArrayList<>();
        idList.add("myid1"); idList.add("myid2"); idList.add("myid3");
        assertEquals("MON myid1 myid2 myid3\r\n", MessageBuilder.buildMon(idList));
        idList.remove(0);
        assertEquals("MON myid2 myid3\r\n", MessageBuilder.buildMon(idList));
        idList.remove(0);
        assertEquals("MON myid3\r\n", MessageBuilder.buildMon(idList));
    }
    @Test
    void RequestBuildTest() {
        assertEquals("REQUEST myid1\r\n", MessageBuilder.buildRequest("myid1"));
    }
    @Test
    void RespondBuildTest() {
        Url url = new Url("https",null,null,"salute.sal",-1, "/ezajo");
        assertEquals("RESPOND myid1 https://salute.sal/ezajo ALARM\r\n", MessageBuilder.buildRespond("myid1",url,"ALARM"));
    }
    @Test
    void SetupBuildTest() {
        Url url = new Url("https",null,null,"salute.sal",-1, "/ezajo");
        Aurl aurl1 = new Aurl("myid1",url,12345,56789);
        Aurl aurl2 = new Aurl("myid2",url,12345,56789);
        List<Aurl> aurlList = List.of(aurl1,aurl2);
        assertEquals("SETUP freque myid1!https://salute.sal/ezajo!12345!56789 myid2!https://salute.sal/ezajo!12345!56789\r\n", MessageBuilder.buildSetup("freque",aurlList));
    }
    @Test
    void StatusofBuildTest(){
        assertEquals("STATUSOF myid1\r\n", MessageBuilder.buildStatusof("myid1"));
    }
    @Test
    void StatusBuildTest(){
        assertEquals("STATUS myid1 ALARM\r\n", MessageBuilder.buildStatus("myid1", "ALARM"));
    }
    @Test
    void ProbeBuildTest(){
        assertEquals("PROBE https 25565\r\n", MessageBuilder.buildProbe("https", 25565));
    }
    @Test
    void DataBuildTest(){
        assertEquals("DATA https 25565\r\n", MessageBuilder.buildData("https", 25565));
    }
}
