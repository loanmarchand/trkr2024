package org.helmo;


import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;

import static org.helmo.ImapBuilder.*;

public class ProbeImapRunable implements Runnable , ProbeRunable{
    private BufferedReader in;
    private PrintWriter out;
    private final Probe probe;
    private final Map<Aurl, String> aurlsStatus;
    private int frequency;

    public ProbeImapRunable(Socket socket, Probe probe) {
        this.probe = probe;
        this.aurlsStatus = new HashMap<>();
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
        checkUnreadEmails();
    }

    public  void checkUnreadEmails( ) {
        // Build a new authorized API client service.
        try {
            final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
            Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            // Request a list of all the messages in the user's mailbox.
            String user = "me"; // Special value "me" can be used to indicate the authenticated user.
            ListMessagesResponse messagesResponse = service.users().messages().list(user).setQ("is:unread").execute();
            List<Message> messages = messagesResponse.getMessages();
            //SI le nombre de messages non lus est supérieur à 10 pas ok sinon ok
            if (messages.size() > 10) {
                System.out.println("IMAP: KO");
            } else {
                System.out.println("IMAP: OK");
            }

        } catch (Exception e) {
            System.out.println("IMAP: DOWN");
        }
    }


    @Override
    public void updateProbe(Socket socket) {
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
            run();
        } catch (Exception e) {
            System.out.println("Erreur lors de la création du BufferedReader et du PrintWriter: " + e.getMessage());
        }
    }
}
