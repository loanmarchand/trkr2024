package org.helmo;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ConfigMonitor {
    private final String multicastAdress;
    private final int multicastPort;
    private  String multicastInterface;
    private final int clientPort;
    private final boolean tls;
    private final String aesKey;
    private final Map<String, String> protocolsDelay;
    private final List<Aurl> probes;

    public ConfigMonitor(
            String multicastAdress, //"multicastAddress": "224.0.0.254",
            int multicastPort, // "multicastPort": 65001,
            String multicastInterface, // "multicastInterface": "monInterfaceReseau_12345",
            int clientPort, // "clientPort": 12345,
            boolean tls, // // "tls": false,
            String aesKey, //"aesKey": "MaCleAESGenereeParMesSoins",
            Map<String, String> protocolsDelay, // "protocolsDelay": { "snmp": 120, "https": 120 },
            List<Aurl> probes // "probes": {"http1": "http1!https://www.swilabus.com/!0!1500", ....
    ) {
        this.multicastAdress = multicastAdress;
        this.multicastPort = multicastPort;
        this.multicastInterface = multicastInterface;
        this.clientPort = clientPort;
        this.tls = tls;
        this.aesKey = aesKey;
        this.protocolsDelay = protocolsDelay;
        this.probes = probes;
    }

    public String multicastAdress() {
        return multicastAdress;
    }

    public void setMulticastInterface(String multicastInterface) {
        this.multicastInterface = multicastInterface;
    }

    public int multicastPort() {
        return multicastPort;
    }

    public String multicastInterface() {
        return multicastInterface;
    }

    public int clientPort() {
        return clientPort;
    }

    public boolean tls() {
        return tls;
    }

    public String aesKey() {
        return aesKey;
    }

    public Map<String, String> protocolsDelay() {
        return protocolsDelay;
    }

    public List<Aurl> probes() {
        return probes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ConfigMonitor) obj;
        return Objects.equals(this.multicastAdress, that.multicastAdress) &&
                this.multicastPort == that.multicastPort &&
                Objects.equals(this.multicastInterface, that.multicastInterface) &&
                this.clientPort == that.clientPort &&
                this.tls == that.tls &&
                Objects.equals(this.aesKey, that.aesKey) &&
                Objects.equals(this.protocolsDelay, that.protocolsDelay) &&
                Objects.equals(this.probes, that.probes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(multicastAdress, multicastPort, multicastInterface, clientPort, tls, aesKey, protocolsDelay, probes);
    }

    @Override
    public String toString() {
        return "ConfigMonitor[" +
                "multicastAdress=" + multicastAdress + ", " +
                "multicastPort=" + multicastPort + ", " +
                "multicastInterface=" + multicastInterface + ", " +
                "clientPort=" + clientPort + ", " +
                "tls=" + tls + ", " +
                "aesKey=" + aesKey + ", " +
                "protocolsDelay=" + protocolsDelay + ", " +
                "probes=" + probes + ']';
    }

}