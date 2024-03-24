package org.helmo;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Probe {
    private ServerSocket serverSocket;
    private boolean running;
    private final ScheduledExecutorService scheduler;
    private AbstractProbeTask abstractProbeTask = null;
    private final ConfigProbes configProbes;

    public Probe(ConfigProbes configProbes) {
        this.configProbes = configProbes;
        this.running = false;

        this.scheduler = Executors.newScheduledThreadPool(3);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Stopping the probe...");
            stop();
        }));
    }

    public void start() {
        System.out.println("Starting the probe");
        System.out.println("Protocol: " + configProbes.protocol());

        if (!initializeServerSocket()) {
            stop();
            return;
        }

        running = true;
        String message = MessageBuilder.buildProbe(configProbes.protocol(), configProbes.unicastPort());
        startThreadLoop(() -> sendMulticastMessage(message), configProbes.multicastDelay());
        while (running) {
            handleConnection();
        }
    }





    public void stop() {
        running = false;
        scheduler.shutdownNow();
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing the server socket: " + e.getMessage());
            }
        }
        System.out.println("Probe stopped.");
    }


    public void startThreadLoop(Runnable runnable, long delay) {
        scheduler.scheduleWithFixedDelay(() -> {
            if (running) {
                runnable.run();
            }
        }, 0, delay, TimeUnit.SECONDS);
    }
    public void sendMulticastMessage(String message) {
        try (MulticastSocket socket = new MulticastSocket(configProbes.multicastPort())) {
            socket.setNetworkInterface(NetworkInterface.getByName(configProbes.multicastInterface()));
            socket.setBroadcast(true);
            byte[] buffer = message.getBytes();
            InetAddress group = InetAddress.getByName(configProbes.multicastAddress());
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, configProbes.multicastPort());
            socket.send(packet);
            //Afiicher l'ip et le port du groupe multicast
            System.out.println("Adresse du groupe multicast: " + group.getHostAddress());
            System.out.println("Port du groupe multicast: " + configProbes.multicastPort());
            System.out.println("Annonce multicast envoyée : " + message);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi de l'annonce multicast : " + e.getMessage());
        }
    }
    private boolean initializeServerSocket() {
        try {
            serverSocket = new ServerSocket(configProbes.unicastPort());
            System.out.println("Server Socket created on port " + configProbes.unicastPort());
            return true;
        } catch (IOException e) {
            System.out.println("Unable to create the server socket on port " + configProbes.unicastPort() + ": " + e.getMessage());
            return false;
        }
    }

    private void handleConnection() {
        try{
            //Afficher le nombre de clients connectés
            Socket socket = serverSocket.accept();
            switch (configProbes.protocol()) {
                case "http":
                    if (abstractProbeTask == null) {
                        abstractProbeTask = new HttpClientProbeTask(socket, this);
                        new Thread(abstractProbeTask).start();
                    }
                    else {
                        abstractProbeTask.updateProbe(socket);
                    }
                    break;
                case "snmp":
                    if (abstractProbeTask == null) {
                        abstractProbeTask = new SnmpClientProbeTask(socket, this);
                        new Thread(abstractProbeTask).start();
                    }
                    else {
                        abstractProbeTask.updateProbe(socket);
                    }
                    break;
                case "imap":
                    if (abstractProbeTask == null) {
                        abstractProbeTask = new ImapClientProbetask(socket, this);
                        new Thread(abstractProbeTask).start();
                    }
                    else {
                        abstractProbeTask.updateProbe(socket);
                    }
                    break;
            }
        } catch (IOException e) {
            System.out.println("Erreur lors de l'acceptation de la connexion : " + e.getMessage());
        }
    }

    public ConfigProbes getConfigProbes() {
        return configProbes;
    }
}
