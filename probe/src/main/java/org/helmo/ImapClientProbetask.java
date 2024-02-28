package org.helmo;


import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

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
            Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT,probe.url().host()))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            // Request a list of all the messages in the user's mailbox.
            String user = "me"; // Special value "me" can be used to indicate the authenticated user.
            ListMessagesResponse messagesResponse = service.users().messages().list(user).setQ("is:unread").execute();
            List<Message> messages = messagesResponse.getMessages();
            //SI le nombre de messages non lus est supérieur à 10 pas ok sinon ok
            if (messages.size() > 10) {
                if (!status.equals("ALARM")) {
                    System.out.println("ALARM");
                    aurlsStatus.put(probe, "ALARM");
                    return true;
                }
            } else {
                if (!status.equals("OK")) {
                    System.out.println("OK");
                    aurlsStatus.put(probe, "OK");
                    return true;
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
            if (!status.equals("DOWN")) {
                System.out.println("DOWN");
                aurlsStatus.put(probe, "DOWN");
                return true;
            }
        }
        return false;
    }
}
