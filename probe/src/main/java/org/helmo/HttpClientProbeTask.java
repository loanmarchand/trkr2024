package org.helmo;

import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class HttpClientProbeTask extends AbstractProbeTask {
    private final HttpClient client;

    public HttpClientProbeTask(Socket socket, Probe probe) {
        super(socket, probe);
        this.client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).followRedirects(HttpClient.Redirect.NORMAL).connectTimeout(Duration.ofSeconds(10)).build();
    }


    protected void collectData() {
        aurlsStatus.keySet().forEach(aurl -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(aurl.url().protocol() + "://" + aurl.url().host()))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            Instant start = Instant.now(); // Marquer le début de la requête

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApplyAsync(response -> setStatut(aurl, response, start))
                    .exceptionally(e -> {
                        System.err.println("Erreur lors de la collecte des données pour l'URL " + aurl.url().host() + " : " + e.getMessage());
                        return false; // En cas d'exception, considérer qu'aucune modification n'a été effectuée
                    })
                    .thenAccept(isChanged -> {
                        if (isChanged) {
                            String message = MessageBuilder.buildData(probe.getConfigProbes().protocol(), probe.getConfigProbes().unicastPort());
                            probe.sendMulticastMessage(message);
                        }
                    });
        });
    }

    private Boolean setStatut(Aurl aurl, HttpResponse<String> response, Instant start) {
        Instant finish = Instant.now(); // Marquer la fin de la requête
        long timeElapsed = Duration.between(start, finish).toMillis(); // Calculer le temps écoulé en millisecondes
        System.out.println("Réponse reçue : " + response.statusCode() + " en " + timeElapsed + " ms");

        String status;
        if (response.statusCode() == 200 || response.statusCode() == 307) {
            status = timeElapsed < aurl.max() - aurl.min() ? "OK" : "ALARM";
        } else {
            status = "DOWN";
        }

        synchronized (this) {
            if (!Objects.equals(aurlsStatus.get(aurl), status)) {
                aurlsStatus.put(aurl, status);
                return true;
            }
        }
        return false;
    }

}
