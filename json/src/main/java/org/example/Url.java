package org.example;

public record Url(String protocol, String user, String password, String host, int port, String path) {
}
