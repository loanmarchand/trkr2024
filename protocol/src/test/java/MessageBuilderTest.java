import org.helmo.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MessageBuilderTest {

    @Test
    void NewmonBuildTest() {
        assertEquals("NEWMON monid!https://salute.sal/ezajo!57575!54645654\r\n", MessageBuilder.buildNewmon("monid!https://salute.sal/ezajo!57575!54645654"));
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
}
