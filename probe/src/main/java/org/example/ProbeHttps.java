package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProbeHttps extends Probe {
    private ServerSocket serverSocket;
    private final HttpClient client;
    private boolean running;
    private final ScheduledExecutorService scheduler;
    private final ConfigMonitor configMonitor;

    public ProbeHttps(ConfigMonitor configMonitor, ConfigProbes configProbes) {
        super(configProbes);
        this.client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).followRedirects(HttpClient.Redirect.NORMAL).connectTimeout(Duration.ofSeconds(10)).build();
        this.running = false;
        this.configMonitor = configMonitor;
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
        startThreadLoop(this::sendMulticastAnnouncement, 90);
        startThreadLoop(this::waitForConfig, 90);
        startThreadLoop(this::collectData, Long.parseLong(configMonitor.protocolsDelay().get("https")));

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
        for (Aurl aurl : configMonitor.probes()) {
            if (aurl.type().contains("http")) {
                collectData(aurl);
            }
        }
    }

    private void startThreadLoop(Runnable runnable, long delay) {
        scheduler.scheduleWithFixedDelay(() -> {
            if (running) {
                runnable.run();
            }
        }, 0, delay, TimeUnit.SECONDS);
    }

    private void collectData(Aurl aurl) {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(aurl.url().protocol() + "://" + aurl.url().host()))//TODO changer l'URI
                    .timeout(Duration.ofSeconds(5)).GET().build();

            Instant start = Instant.now(); // Marquer le début de la requête
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Instant finish = Instant.now(); // Marquer la fin de la requête

            long timeElapsed = Duration.between(start, finish).toMillis(); // Calculer le temps écoulé en millisecondes
            System.out.println("Réponse reçue : " + response.statusCode() + " en " + timeElapsed + " ms");
            // Ici, vous pouvez traiter la réponse, par exemple vérifier le code de statut

        } catch (Exception e) {
            System.out.println("Erreur lors de la collecte des données : " + e.getMessage());
        }
    }

    private void sendMulticastAnnouncement() {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            String message = "Sonde HTTPS démarrée : ";//TODO changer le message
            byte[] buffer = message.getBytes();
            InetAddress group = InetAddress.getByName(configProbes.multicastAddress());
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, configProbes.multicastPort());
            socket.send(packet);
            System.out.println("Annonce multicast envoyée.");
        } catch (IOException e) {
            System.out.println("Erreur lors de l'envoi de l'annonce multicast : " + e.getMessage());
        }
    }

    private void waitForConfig() {
        try {
            serverSocket.setSoTimeout(90000); // Attendre la connexion pendant l'intervalle de l'annonce multicast
            System.out.println("En attente de la configuration...");
            try (Socket socket = serverSocket.accept(); BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String configLine;
                configLine = reader.readLine();
                System.out.println("Configuration reçue: " + configLine);
                // Ici, vous pouvez traiter l'ensemble de la configuration reçue
                /*
                  Exemple de traitement de la configuration reçue
                  1. Vérifier si la configuration est valide -> analyse.isValid(configLine)
                  2. Liste des aurls -> analyse.getAurls(configLine)
                 3. Metre a jour la configuration
                 */
            } catch (SocketTimeoutException e) {
                System.out.println("Aucune configuration reçue dans l'intervalle actuel.");
            }
        } catch (IOException e) {
            System.out.println("Erreur lors de l'attente de la configuration: " + e.getMessage());
        }
    }
}
