package org.helmo;

import java.util.Objects;

public final class ConfigProbes {
    private final String protocol;
    private final String multicastAddress;
    private final int multicastPort;
    private String multicastInterface;
    private final int multicastDelay;
    private final int unicastPort;
    private final String aesKey;

    public ConfigProbes(
            String protocol,
            String multicastAddress,
            int multicastPort,
            String multicastInterface,
            int multicastDelay,
            int unicastPort,
            String aesKey) {
        this.protocol = protocol;
        this.multicastAddress = multicastAddress;
        this.multicastPort = multicastPort;
        this.multicastInterface = multicastInterface;
        this.multicastDelay = multicastDelay;
        this.unicastPort = unicastPort;
        this.aesKey = aesKey;
    }

    public String protocol() {
        return protocol;
    }

    public String multicastAddress() {
        return multicastAddress;
    }

    public int multicastPort() {
        return multicastPort;
    }

    public String multicastInterface() {
        return multicastInterface;
    }

    public void setMulticastInterface(String multicastInterface) {
        this.multicastInterface = multicastInterface;
    }

    public int multicastDelay() {
        return multicastDelay;
    }

    public int unicastPort() {
        return unicastPort;
    }

    public String aesKey() {
        return aesKey;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ConfigProbes) obj;
        return Objects.equals(this.protocol, that.protocol) &&
                Objects.equals(this.multicastAddress, that.multicastAddress) &&
                this.multicastPort == that.multicastPort &&
                Objects.equals(this.multicastInterface, that.multicastInterface) &&
                this.multicastDelay == that.multicastDelay &&
                this.unicastPort == that.unicastPort &&
                Objects.equals(this.aesKey, that.aesKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(protocol, multicastAddress, multicastPort, multicastInterface, multicastDelay, unicastPort, aesKey);
    }

    @Override
    public String toString() {
        return "ConfigProbes[" +
                "protocol=" + protocol + ", " +
                "multicastAddress=" + multicastAddress + ", " +
                "multicastPort=" + multicastPort + ", " +
                "multicastInterface=" + multicastInterface + ", " +
                "multicastDelay=" + multicastDelay + ", " +
                "unicastPort=" + unicastPort + ", " +
                "aesKey=" + aesKey + ']';
    }

}
