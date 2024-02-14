package org.helmo.probe;

public class ProbeHttpsTest {
    public static void main(String[] args) {
        ProbeHttps probe = new ProbeHttps("https://www.swilabus.be", 5);
        probe.start();
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        probe.stop();
    }
}
