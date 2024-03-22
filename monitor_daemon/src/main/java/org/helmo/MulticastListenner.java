package org.helmo;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class MulticastListenner implements Runnable{
    private final ConfigMonitor configMonitor;
    private final MulticastSocket multicastSocket;
    private final BlockingQueue<Runnable> worker;
    private final MonitorDaemon monitorDaemon;

    public MulticastListenner(ConfigMonitor configMonitor, BlockingQueue<Runnable> worker, MonitorDaemon monitorDaemon) {
        try {
            InetAddress group = InetAddress.getByName(configMonitor.multicastAdress());
            this.multicastSocket = new MulticastSocket(configMonitor.multicastPort());
            NetworkInterface networkInterface = NetworkInterface.getByName(configMonitor.multicastInterface());
            this.multicastSocket.joinGroup(new InetSocketAddress(group, configMonitor.multicastPort()), networkInterface);
            //Print l'adresse et le port du groupe multicast
            System.out.println("Adresse du groupe multicast: " + group.getHostAddress());
            System.out.println("Port du groupe multicast: " + configMonitor.multicastPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.worker = worker;
        this.configMonitor = configMonitor;
        this.monitorDaemon = monitorDaemon;
    }


    private void handleMulticastMessage(String message, InetAddress probeAddress) {
    worker.offer(()->{
        Command command = MessageAnalyzer.analyzeMessage(message);
        if (command != null) {
            System.out.println("Commande reconnue: " + command.getCommandType());
            switch (command.getCommandType()) {
                case "PROBE":
                    worker.offer(()->handleProbeCommand(command, probeAddress));
                    break;
                case "DATA":
                    handleDataCommand(command, probeAddress);
                    break;
                default:
                    System.out.println("Commande non reconnue");
                    break;
            }
        }
    });
    }

    private void handleDataCommand(Command command, InetAddress probeAddress) {
        List<Aurl> aurlsStatus = configMonitor.probes().stream()
                .filter(aurl -> aurl.type().contains(command.getProtocole()))
                .toList();
        aurlsStatus.forEach(aurl -> this.worker.offer(() -> monitorDaemon.sendStatusOfAurl(aurl, command, probeAddress)));
    }

    private void handleProbeCommand(Command command, InetAddress probeAddress) {
        List<Aurl> aurls = configMonitor.probes().stream().filter(aurl -> aurl.type().contains(command.getProtocole())).toList();
        monitorDaemon.sendAurlsToProbes(aurls, command, probeAddress);
    }

    @Override
    public void run() {
            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    multicastSocket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    InetAddress probeAddress = packet.getAddress();
                    System.out.println("Received multicast message: " + message);
                    handleMulticastMessage(message, probeAddress);
                } catch (IOException e) {
                    System.out.println("IOException in listenForMulticast: " + e.getMessage());
                    break;
                }
            }
    }
}
