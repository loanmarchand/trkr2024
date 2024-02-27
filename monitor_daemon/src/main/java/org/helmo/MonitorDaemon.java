package org.helmo;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonitorDaemon {
    private final ConfigMonitor configMonitor;
    private MulticastSocket multicastSocket;
    private final AesEncryption aesEncryption;
    private final Map<Aurl,String> aurlStatus;

    public MonitorDaemon(ConfigMonitor configMonitor) {
        this.configMonitor = configMonitor;
        this.aesEncryption = new AesEncryption();
        this.aurlStatus = new HashMap<>();
        configMonitor.probes().forEach(aurl -> aurlStatus.put(aurl,"UNKNOWN"));
    }

    public void start() {
        System.out.println("Starting the monitor daemon");
        try {
            InetAddress group = InetAddress.getByName(configMonitor.multicastAdress());
            this.multicastSocket = new MulticastSocket(configMonitor.multicastPort());
            NetworkInterface networkInterface = NetworkInterface.getByName(configMonitor.multicastInterface());
            this.multicastSocket.joinGroup(new InetSocketAddress(group, configMonitor.multicastPort()), networkInterface);
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
                    multicastSocket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    InetAddress probeAddress = packet.getAddress();
                    System.out.println("Received multicast message: " + message);
                    processMultiCastMessage(message, probeAddress);
                } catch (IOException e) {
                    System.out.println("IOException in listenForMulticast: " + e.getMessage());
                    break;
                }
            }
        }).start();
    }

    private void processMultiCastMessage(String message, InetAddress probeAddress) {
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
                    List<Aurl> aurlsStatus = configMonitor.probes().stream()
                            .filter(aurl -> aurl.type().contains(command.getProtocole()))
                            .toList();
                    aurlsStatus.forEach(aurl -> sendStatusOfAurl(aurl,command, probeAddress));
                    break;
                default:
                    System.out.println("Commande non reconnue");
                    break;
            }
        }
    }

    private void sendStatusOfAurl(Aurl aurl, Command command, InetAddress probeAddress) {
        // Example of sending the status of an AURL to the probe
        try (Socket socket = new Socket(probeAddress, Integer.parseInt(command.getPort()));
             PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true)) {
            String message = MessageBuilder.buildStatusof(aurl.type());
            System.out.println("Sending status of AURL to probe: " + message);
            message = aesEncryption.encrypt(message, configMonitor.aesKey());
            out.print(message);
        } catch (IOException e) {
            System.out.println("Error sending status of AURL to probe: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleStatusOfResponse(Aurl aurl, Socket socket) {
        try {
            String response = new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine();
            response = aesEncryption.decrypt(response, configMonitor.aesKey());
            Command command = MessageAnalyzer.analyzeMessage(response);
            if (command != null && command.getCommandType().equals("STATUS")) {
                aurlStatus.put(aurl, command.getState());
            }
        } catch (IOException e) {
            System.out.println("Error handling status of AURL response: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error handling status of AURL response: " + e.getMessage());
        }
    }

    private void sendAurlsToProbes(List<Aurl> aurls, Command command, InetAddress probeAddress) {
        System.out.println(command);
        // Example of sending AURLs to the probe
        try (Socket socket = new Socket(probeAddress, Integer.parseInt(command.getPort()));
             PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true)) {
            String message = MessageBuilder.buildSetup(configMonitor.protocolsDelay().get(command.getProtocole()), aurls);
            System.out.println("Sending AURLs to probe: " + message);
            message = aesEncryption.encrypt(message, configMonitor.aesKey());
            out.print(message);
        } catch (IOException e) {
            System.out.println("Error sending AURLs to probe: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        JsonHelper reader = new JsonHelper();
        ConfigMonitor configMonitor = reader.readConfigMonitor("json/src/main/resources/config-monitor.json");
        MonitorDaemon monitorDaemon = new MonitorDaemon(configMonitor);
        monitorDaemon.start();
    }
}
