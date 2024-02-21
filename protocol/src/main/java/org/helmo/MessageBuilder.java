package org.helmo;

public class MessageBuilder {

    public static String buildProbeMessage(String protocol, int port) {
        return Protocole.getPROBE_MSG().replace("<protocol>", protocol).replace("<port>", String.valueOf(port));
    }
    }
