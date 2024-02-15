package org.helmo;

public record ConfigProbes(
        String protocol,
        String multicastAddress,
        int multicastPort,
        String multicastInterface,
        int multicastDelay,
        int unicastPort,
        String aesKey) {
}
