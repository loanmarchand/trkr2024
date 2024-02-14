package org.helmo.probe;

import org.helmo.probe.Probe;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProbeHttps extends Probe {
    private final HttpClient client;
    private boolean running;
    private ConfigProbes configProbes;
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
    }

    @Override
    public void stop() {
        System.out.println("Arrêt du probe HTTPS pour l'URL : " + servicesURL);
        running = false;
    }

    @Override
    protected void collectData() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(servicesURL))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // Ici, vous pouvez traiter la réponse, par exemple vérifier le code de statut
            System.out.println("Réponse reçue : " + response.statusCode());
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
    }
}
