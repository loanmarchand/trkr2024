package org.helmo.probe;

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
    private final ConfigProbes configProbes;
    private final ScheduledExecutorService scheduler;

    public ProbeHttps(String servicesURL, int pollingInterval, ConfigProbes configProbes) {
        super(servicesURL, pollingInterval);
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.running = false;
        this.configProbes = configProbes;
        scheduler = Executors.newScheduledThreadPool(1);
    }
    @Override
    public void start() {
        System.out.println("Démarrage du probe HTTPS pour l'URL : " + servicesURL);
        try {
            serverSocket = new ServerSocket(configProbes.unicastPort());
            System.out.println("Server Socket créé sur le port " + configProbes.unicastPort());
        } catch (IOException e) {
            System.out.println("Impossible de créer le server socket sur le port " + configProbes.unicastPort() + " : " + e.getMessage());
            return;
        }
        running = true;
        scheduler.scheduleAtFixedRate(this::sendMulticastAnnouncement, 0, 90, TimeUnit.SECONDS);
        // Utiliser un thread pour gérer la boucle de collecte périodique
        new Thread(() -> {
            while (running) {
                collectData();
                try {
                    Thread.sleep(pollingInterval * 1000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Probe interrompu");
                }
            }
        }).start();
        new Thread(this::waitForConfig).start();
    }

    private void waitForConfig() {
        try (Socket socket = serverSocket.accept();
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String configLine;
            StringBuilder configData = new StringBuilder();
            while ((configLine = reader.readLine()) != null) {
                configData.append(configLine).append("\n");
                // Traitez ici la ligne de configuration si nécessaire
            }
            System.out.println("Configuration reçue: " + configData);
            // Ici, vous pouvez traiter l'ensemble de la configuration reçue
        } catch (IOException e) {
            System.out.println("Erreur lors de la réception de la configuration: " + e.getMessage());
        }
        // Après avoir reçu la configuration, la connexion se ferme automatiquement grâce au try-with-resources
    }


    @Override
    public void stop() {
        System.out.println("Arrêt du probe HTTPS pour l'URL : " + servicesURL);
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("Erreur lors de la fermeture du server socket: " + e.getMessage());
            }
        }
    }

    @Override
    protected void collectData() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(servicesURL))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

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
            String message = "Sonde HTTPS démarrée : " + servicesURL;//TODO changer le message
            byte[] buffer = message.getBytes();
            InetAddress group = InetAddress.getByName(configProbes.multicastAddress());
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, configProbes.multicastPort());
            socket.send(packet);
            System.out.println("Annonce multicast envoyée.");
        } catch (IOException e) {
            System.out.println("Erreur lors de l'envoi de l'annonce multicast : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ConfigProbes configProbes = new ConfigProbes("https","224.0.0.254",65001,"wan0",10,65002,"wan0");
        String url = "https://www.google.com";
        Probe probe = new ProbeHttps(url, 10, configProbes);
        probe.start();

        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        probe.stop();
    }
}
