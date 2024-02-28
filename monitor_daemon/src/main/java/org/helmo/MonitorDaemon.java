package org.helmo;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MonitorDaemon {
    private final ConfigMonitor configMonitor;
    private MulticastSocket multicastSocket;
    private final AesEncryption aesEncryption;
    private final Map<Aurl, String> aurlStatus;
    private final BlockingQueue<Runnable> worker;
    private final ExecutorService executor;

    public MonitorDaemon(ConfigMonitor configMonitor) {
        this.configMonitor = configMonitor;
        this.aesEncryption = new AesEncryption();
        this.aurlStatus = new HashMap<>();
        this.worker = new LinkedBlockingQueue<>();
        this.executor = new ThreadPoolExecutor(10,50,60, TimeUnit.SECONDS,worker);
        configMonitor.probes().forEach(aurl -> aurlStatus.put(aurl, "UNKNOWN"));
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
                    handleProbeMessage(message, probeAddress);
                } catch (IOException e) {
                    System.out.println("IOException in listenForMulticast: " + e.getMessage());
                    break;
                }
            }
        }).start();
    }

    private void handleProbeMessage(String message, InetAddress probeAddress) {
        executor.submit(()->{
            Command command = MessageAnalyzer.analyzeMessage(message);
            if (command != null) {
                System.out.println("Commande reconnue: " + command.getCommandType());
                switch (command.getCommandType()) {
                    case "PROBE":
                        List<Aurl> aurls = configMonitor.probes().stream().filter(aurl -> aurl.type().contains(command.getProtocole())).toList();
                        sendAurlsToProbes(aurls, command, probeAddress);
                        break;
                    case "DATA":
                        List<Aurl> aurlsStatus = configMonitor.probes().stream()
                                .filter(aurl -> aurl.type().contains(command.getProtocole()))
                                .toList();
                        aurlsStatus.forEach(aurl -> this.worker.offer(() -> sendStatusOfAurl(aurl, command, probeAddress)));
                        break;
                    case "STATUS":
                        updateAurlStatus(command);
                        break;

                    default:
                        System.out.println("Commande non reconnue");
                        break;
                }
            }
        });

    }

    private void updateAurlStatus(Command command) {
        String id = command.getId();
        String state = command.getState();
        aurlStatus.keySet().stream()
                .filter(a -> a.type().equals(id))
                .findFirst().ifPresent(aurl -> aurlStatus.put(aurl, state));
    }


    private void sendStatusOfAurl(Aurl aurl, Command command, InetAddress probeAddress) {
        try (Socket socket = new Socket(probeAddress, Integer.parseInt(command.getPort()))) {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String message = MessageBuilder.buildStatusof(aurl.type());
            message = aesEncryption.encrypt(message, configMonitor.aesKey());
            out.println(message);
            out.flush();

            System.out.println("STATUSOF sent to probe: " + message);

            // Attente de la réponse STATUS
            String encryptedResponse = in.readLine();
            if (encryptedResponse != null) {
                String response = aesEncryption.decrypt(encryptedResponse, configMonitor.aesKey());
                handleProbeMessage(response, probeAddress);
            } else {
                System.out.println("No response received from probe");
            }
        } catch (IOException e) {
            System.out.println("Error communicating with probe at address " + probeAddress.getHostAddress() + ": " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void sendAurlsToProbes(List<Aurl> aurls, Command command, InetAddress probeAddress) {
        System.out.println(command);
        // Example of sending AURLs to the probe
        try (Socket socket = new Socket(probeAddress, Integer.parseInt(command.getPort())); PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true)) {
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
