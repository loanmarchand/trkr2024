package org.helmo;


import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import static org.helmo.ImapBuilder.*;

public class ImapClientProbetask extends AbstractProbeTask {

    public ImapClientProbetask(Socket socket, Probe probe) {
        super(socket, probe);
    }

    protected void collectData() {
        aurlsStatus.keySet().forEach(aurl -> {
            if (checkUnreadEmails(aurl, aurlsStatus.get(aurl))) {
                String message = MessageBuilder.buildData(probe.getConfigProbes().protocol(), probe.getConfigProbes().unicastPort());
                probe.sendMulticastMessage(message);
            }
        });
    }

    public boolean checkUnreadEmails(Aurl probe, String status) {
        // Build a new authorized API client service.
        try {
            final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
            Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT,CredentialsParser.base64ToCredentials(probe.url().host())))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            // Request a list of all the messages in the user's mailbox.
            String user = probe.url().user(); // Special value "me" can be used to indicate the authenticated user.
            ListMessagesResponse messagesResponse = service.users().messages().list(user).setQ("is:unread").execute();
            List<Message> messages = messagesResponse.getMessages();
            //SI le nombre de messages non lus est supérieur à 10 pas ok sinon ok
            if (messages.size() > 10) {
                if (!status.equals("ALARM")) {
                    aurlsStatus.put(probe, "ALARM");
                    return true;
                }
            } else {
                if (!status.equals("OK")) {
                    aurlsStatus.put(probe, "OK");
                    return true;
                }
            }

        } catch (Exception ignored) {
            if (!status.equals("DOWN")) {
                aurlsStatus.put(probe, "DOWN");
                return true;
            }
        }
        return false;
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
