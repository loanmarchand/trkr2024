package org.helmo.probe;

import org.helmo.probe.Probe;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ProbeHttps extends Probe {
    private final HttpClient client;
    private boolean running;

    public ProbeHttps(String servicesURL, int pollingInterval) {
        super(servicesURL, pollingInterval);
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.running = false;
    }
    @Override
    public void start() {
        System.out.println("Démarrage du probe HTTPS pour l'URL : " + servicesURL);
        running = true;
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
}
