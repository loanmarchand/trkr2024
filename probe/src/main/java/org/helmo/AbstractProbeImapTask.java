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

public class AbstractProbeImapTask extends AbstractProbeTask {

    public AbstractProbeImapTask(Socket socket, Probe probe) {
        super(socket, probe);
    }

    protected void collectData() {
        checkUnreadEmails();
    }

    public void checkUnreadEmails() {
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
