package org.helmo;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProbeHttps extends Probe {
    private ServerSocket serverSocket;
    private boolean running;
    private final ScheduledExecutorService scheduler;

    public ProbeHttps(ConfigProbes configProbes) {
        super(configProbes);
        this.running = false;

        this.scheduler = Executors.newScheduledThreadPool(3);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Stopping the probe...");
            stop();
        }));
    }

    @Override
    public void start() {
        System.out.println("Starting the HTTPS probe");

        if (!initializeServerSocket()) {
            stop();
            return;
        }

        running = true;
        String message = MessageBuilder.buildProbeMessage(configProbes.protocol(), configProbes.unicastPort());
        startThreadLoop(() -> sendMulticastMessage(message), configProbes.multicastDelay());
        while (running) {
            handleConnection();
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


    @Override
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

    @Override
    protected void collectData() {

    }


    public void startThreadLoop(Runnable runnable, long delay) {
        scheduler.scheduleWithFixedDelay(() -> {
            if (running) {
                runnable.run();
            }
        }, 0, delay, TimeUnit.SECONDS);
    }
    public void sendMulticastMessage(String message) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            byte[] buffer = message.getBytes();
            InetAddress group = InetAddress.getByName(configProbes.multicastAddress());
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, configProbes.multicastPort());
            socket.send(packet);
            System.out.println("Annonce multicast envoyée : " + message);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi de l'annonce multicast : " + e.getMessage());
        }
    }

    private void handleConnection() {
        try{
            //Afficher le nombre de clients connectés
            System.out.println("Nombre de clients connectés: " + serverSocket.getSoTimeout());
            Socket socket = serverSocket.accept();
            ProbeHttpsRunable probeHttpsRunable = new ProbeHttpsRunable(socket, this);
            new Thread(probeHttpsRunable).start();
        } catch (IOException e) {
            System.out.println("Erreur lors de l'acceptation de la connexion : " + e.getMessage());
        }
    }
}
