import org.helmo.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BuilderTest {

    @Test
    void NewmonBuildTest() {
        assertTrue(MessageBuilder.buildNewmon("monid!https://salute.sal/ezajo!57575!54645654")
                .equals("NEWMON monid!https://salute.sal/ezajo!57575!54645654\r\n"));
    }
    @Test
    void NewmonrespBuildTest() {
        assertTrue(MessageBuilder.buildNewmonResp("+OK")
                .equals("+OK\r\n"));
        assertTrue(MessageBuilder.buildNewmonResp("-ERR")
                .equals("-ERR\r\n"));
        assertTrue(MessageBuilder.buildNewmonResp("+OK", "Tout es ok")
                .equals("+OK Tout es ok\r\n"));
    }
    @Test
    void ListmonBuildTest() {
        assertTrue(MessageBuilder.buildListmon()
                .equals("LISTMON\r\n"));
    }
    @Test
    void MonBuildTest() {
        List<String> idList = new ArrayList<>();
        idList.add("myid1"); idList.add("myid2"); idList.add("myid3");
        assertTrue(MessageBuilder.buildMon(idList)
                .equals("MON myid1 myid2 myid3\r\n"));
        idList.remove(0);
        assertTrue(MessageBuilder.buildMon(idList)
                .equals("MON myid2 myid3\r\n"));
        idList.remove(0);
        assertTrue(MessageBuilder.buildMon(idList)
                .equals("MON myid3\r\n"));
    }
}
