package org.helmo;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;

public abstract class AbstractProbeTask implements Runnable {
    protected BufferedReader in;
    protected PrintWriter out;
    protected final Probe probe;
    protected final Map<Aurl, String> aurlsStatus;
    protected int frequency;
    protected AesEncryption aesEncryption;

    public AbstractProbeTask(Socket socket, Probe probe) {
        this.probe = probe;
        this.aurlsStatus = new HashMap<>();
        this.aesEncryption = new AesEncryption();
        this.frequency = 0;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

        } catch (IOException e) {
            System.out.println("Erreur lors de la création du BufferedReader et du PrintWriter: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("Attente d'une commande...");
            String configLine;
            configLine = in.readLine();
            configLine = aesEncryption.decrypt(configLine,probe.getConfigProbes().aesKey());
            System.out.println("Configuration reçue: " + configLine);
            Command command = MessageAnalyzer.analyzeMessage(configLine);
            if (command == null || Objects.equals(command.getCommandType(), "NONE")) {
                System.out.println("La configuration reçue est invalide.");
            } else if (Objects.equals(command.getCommandType(), "SETUP")) {
                System.out.println("La configuration reçue est valide.");
                List<Aurl> aurls = new ArrayList<>(command.getAurlList());
                aurls.forEach(aurl -> aurlsStatus.putIfAbsent(aurl, "UNKNOWN"));
                if (frequency == 0) {
                    frequency = Integer.parseInt(command.getFrequency());
                    probe.startThreadLoop(this::collectData, frequency);
                }
            } else if (Objects.equals(command.getCommandType(), "STATUSOF")) {
                String id = command.getId();
                Aurl aurl = aurlsStatus.keySet().stream().filter(a -> a.type().equals(id)).findFirst().orElse(null);
                if (aurl != null) {
                    String message = MessageBuilder.buildStatus(id, aurlsStatus.get(aurl));
                    message = aesEncryption.encrypt(message,probe.getConfigProbes().aesKey());
                    out.println(message); // Ajoutez un retour à la ligne à la fin
                    out.flush();
                    System.out.println("STATUS of URL " + aurl.url().host() + " sent.");
                }
            }


        } catch (SocketTimeoutException e) {
            System.err.println("Aucune configuration reçue dans l'intervalle actuel.");
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture de la configuration: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void collectData();

    public void updateProbe(Socket socket) {
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            run();
        } catch (IOException e) {
            System.out.println("Erreur lors de la mise à jour du BufferedReader et du PrintWriter: " + e.getMessage());
        }
    }
}
