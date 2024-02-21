package org.helmo;

public class MessageBuilder {

    public static String buildProbeMessage(String protocol, int port) {
        return Protocole.getPROBE_MSG().replace("<protocol>", protocol).replace("<port>", String.valueOf(port));
    }

    public static String buildDataMessage(String protocol, int port) {
        return Protocole.getDATA_MSG().replace("<protocol>", protocol).replace("<port>", String.valueOf(port));
    }
}
