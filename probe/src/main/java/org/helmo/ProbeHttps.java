package org.helmo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProbeHttps extends Probe {
    private ServerSocket serverSocket;
    private final HttpClient client;
    private boolean running;
    private final ScheduledExecutorService scheduler;
    private final Map<Aurl, String> aurlsStatus;
    private int frequency;

    public ProbeHttps(ConfigProbes configProbes) {
        super(configProbes);
        this.client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).followRedirects(HttpClient.Redirect.NORMAL).connectTimeout(Duration.ofSeconds(10)).build();
        this.running = false;
        this.aurlsStatus = new HashMap<>();
        this.frequency = 0;
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
        startThreadLoop(this::sendMulticastAnnouncement, configProbes.multicastDelay());
        startThreadLoop(this::waitForConfig, configProbes.multicastDelay());
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
        boolean isChanged = false;
        for (Aurl aurl : aurlsStatus.keySet()) {
                isChanged = collectData(aurl);
        }
        if (isChanged) {
            sendAurlsToMonitor();
        }
    }

    private void sendAurlsToMonitor() {

    }

    private void startThreadLoop(Runnable runnable, long delay) {
        scheduler.scheduleWithFixedDelay(() -> {
            if (running) {
                runnable.run();
            }
        }, 0, delay, TimeUnit.SECONDS);
    }

    private boolean collectData(Aurl aurl) {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(aurl.url().protocol() + "://" + aurl.url().host()))//TODO changer l'URI
                    .timeout(Duration.ofSeconds(5)).GET().build();

            Instant start = Instant.now(); // Marquer le début de la requête
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Instant finish = Instant.now(); // Marquer la fin de la requête

            long timeElapsed = Duration.between(start, finish).toMillis(); // Calculer le temps écoulé en millisecondes
            boolean isChanged = false;
            System.out.println("Réponse reçue : " + response.statusCode() + " en " + timeElapsed + " ms");
            // Ici, vous pouvez traiter la réponse, par exemple vérifier le code de statut
            if (response.statusCode() == 200 || response.statusCode() == 307) {
                if (timeElapsed < aurl.max() - aurl.min()) {
                    //vérifier si le status doit être changé
                    if (!Objects.equals(aurlsStatus.get(aurl), "OK")) {
                        aurlsStatus.put(aurl, "OK");
                        isChanged = true;
                    }
                } else {
                    if (!Objects.equals(aurlsStatus.get(aurl), "SLOW")) {
                        aurlsStatus.put(aurl, "SLOW");
                        isChanged = true;
                    }
                }
            } else {
                if (!Objects.equals(aurlsStatus.get(aurl), "KO")) {
                    aurlsStatus.put(aurl, "KO");
                    isChanged = true;
                }
            }

        } catch (Exception e) {
            System.out.println("Erreur lors de la collecte des données : " + e.getMessage());
        }
        return false;
    }

    private void sendMulticastAnnouncement() {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            String message = MessageBuilder.buildProbeMessage(configProbes.protocol(), configProbes.unicastPort());
            byte[] buffer = message.getBytes();
            InetAddress group = InetAddress.getByName(configProbes.multicastAddress());
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, configProbes.multicastPort());
            socket.send(packet);
            System.out.println("Annonce multicast envoyée : " + message);
        } catch (IOException e) {
            System.out.println("Erreur lors de l'envoi de l'annonce multicast : " + e.getMessage());
        }
    }

    private void waitForConfig() {
        try {
            serverSocket.setSoTimeout(configProbes.multicastDelay() * 1000); // Attendre la connexion pendant l'intervalle de l'annonce multicast
            System.out.println("En attente de la configuration...");
            try (Socket socket = serverSocket.accept(); BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String configLine;
                configLine = reader.readLine();
                System.out.println("Configuration reçue: " + configLine);
                Command command = MessageAnalyzer.analyzeMessage(configLine);
                if (command == null || Objects.equals(command.getCommandType(), "NONE")) {
                    System.out.println("La configuration reçue est invalide.");
                } else if (Objects.equals(command.getCommandType(), "SETUP")) {
                    System.out.println("La configuration reçue est valide.");
                    List<Aurl> aurls = new ArrayList<>();
                    command.getAurlList().forEach(aurl -> aurls.add(new Aurl("test", new Url("", "", "", "", 0, ""), 0, 0)));
                    //TODO:  mettre les vrais valleurs vérifier que le type de aurl est égal a HTTPS
                    aurls.forEach(aurl -> aurlsStatus.putIfAbsent(aurl, "UNKNOWN"));
                    if (frequency == 0) {
                        frequency = Integer.parseInt(command.getFrequency());
                        startThreadLoop(this::collectData, frequency);
                    }
                }
            } catch (SocketTimeoutException e) {
                System.out.println("Aucune configuration reçue dans l'intervalle actuel.");
            }
        } catch (IOException e) {
            System.out.println("Erreur lors de l'attente de la configuration: " + e.getMessage());
        }
    }
}
