package org.helmo;

public class MessageBuilder {

    public static String buildProbe(String protocol, int port) {
        return Protocole.getProbeBuilder().replace("<protocol>", protocol).replace("<port>", String.valueOf(port));
    }
}
