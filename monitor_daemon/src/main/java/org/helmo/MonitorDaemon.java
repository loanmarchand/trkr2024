package org.helmo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.util.List;

public class MonitorDaemon {
    private final ConfigMonitor configMonitor;
    private MulticastSocket socket;

    public MonitorDaemon(ConfigMonitor configMonitor) {
        this.configMonitor = configMonitor;
    }

    public void start() {
        System.out.println("Starting the monitor daemon");
        try {
            InetAddress group = InetAddress.getByName(configMonitor.multicastAdress());
            this.socket = new MulticastSocket(configMonitor.multicastPort());
            NetworkInterface networkInterface = NetworkInterface.getByName(configMonitor.multicastInterface());
            this.socket.joinGroup(new InetSocketAddress(group, configMonitor.multicastPort()), networkInterface);
            listenForMulticast();
        } catch (IOException e) {
            System.out.println("IOException in start: " + e.getMessage());
        }
    }

    private void listenForMulticast() {
        new Thread(() -> {
            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    InetAddress probeAddress = packet.getAddress();
                    System.out.println("Received multicast message: " + message);
                    processMessage(message, probeAddress);
                } catch (IOException e) {
                    System.out.println("IOException in listenForMulticast: " + e.getMessage());
                    break;
                }
            }
        }).start();
    }

    private void processMessage(String message, InetAddress probeAddress) {
        Command command = MessageAnalyzer.analyzeMessage(message);
        if (command != null) {
            System.out.println("Commande reconnue: " + command.getCommandType());
            switch (command.getCommandType()) {
                case "PROBE":
                    List<Aurl> aurls = configMonitor.probes().stream()
                            .filter(aurl -> aurl.type().contains(command.getProtocole()))
                            .toList();
                    sendAurlsToProbes(aurls, command, probeAddress);
                    break;
                case "DATA":
                    // Handle data command
                    break;
                default:
                    System.out.println("Commande non reconnue");
                    break;
            }
        }
    }

    private void sendAurlsToProbes(List<Aurl> aurls, Command command, InetAddress probeAddress) {
        // Example of sending AURLs to the probe
        try (Socket socket = new Socket(probeAddress, Integer.parseInt(command.getPort()));
             PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true)) {
            aurls.forEach(aurl -> out.println(aurl.toString()));
            System.out.println("AURLs sent to probe: " + aurls);
        } catch (IOException e) {
            System.out.println("Error sending AURLs to probe: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        JsonReader reader = new JsonReader();
        ConfigMonitor configMonitor = reader.readConfigMonitor("json/src/main/resources/config-monitor.json");
        MonitorDaemon monitorDaemon = new MonitorDaemon(configMonitor);
        monitorDaemon.start();
    }
}
