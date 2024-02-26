package org.helmo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class ProbeHttpsRunable implements Runnable, ProbeRunable {
    private BufferedReader in;
    private PrintWriter out;
    private final Probe probe;
    private final HttpClient client;
    private final Map<Aurl, String> aurlsStatus;
    private int frequency;

    public ProbeHttpsRunable(Socket socket, Probe probe) {
        this.probe = probe;
        this.aurlsStatus = new HashMap<>();
        this.client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).followRedirects(HttpClient.Redirect.NORMAL).connectTimeout(Duration.ofSeconds(10)).build();
        this.frequency = 0;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            System.out.println("Erreur lors de la création du BufferedReader et du PrintWriter: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("En attente de la configuration...");
            String configLine;
            configLine = in.readLine();
            System.out.println("Configuration reçue: " + configLine);
            Command command = MessageAnalyzer.analyzeMessage(configLine);
            if (command == null || Objects.equals(command.getCommandType(), "NONE")) {
                System.out.println("La configuration reçue est invalide.");
            } else if (Objects.equals(command.getCommandType(), "SETUP")) {
                System.out.println("La configuration reçue est valide.");
                List<Aurl> aurls = new ArrayList<>();
                //TODO : attendre que le builder soit fait pour mettre les vrais valeurs
                command.getAurlList().forEach(aurl -> aurls.add(new Aurl("test", new Url("", "", "", "", 0, ""), 0, 0)));
                //TODO:  mettre les vrais valleurs vérifier que le type de aurl est égal a HTTPS
                aurls.forEach(aurl -> aurlsStatus.putIfAbsent(aurl, "UNKNOWN"));
                if (frequency == 0) {
                    frequency = Integer.parseInt(command.getFrequency());
                    probe.startThreadLoop(this::collectData, frequency);
                }
            } else if (Objects.equals(command.getCommandType(), "STATUSOF")) {
                //TODO : a tester
                String id = command.getId();
                Aurl aurl = aurlsStatus.keySet().stream().filter(a -> a.type().equals(id)).findFirst().orElse(null);
                if (aurl != null) {
                    String message = MessageBuilder.buildStatus(id, aurlsStatus.get(aurl));
                    out.print(message);
                }

            }

        } catch (SocketTimeoutException e) {
            System.err.println("Aucune configuration reçue dans l'intervalle actuel.");
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture de la configuration: " + e.getMessage());
        }
    }

    private void collectData() {
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

    @Override
    public void updateProbe(Socket socket) {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            run();
        } catch (IOException e) {
            System.out.println("Erreur lors de la mise à jour du BufferedReader et du PrintWriter: " + e.getMessage());
        }
    }
}
