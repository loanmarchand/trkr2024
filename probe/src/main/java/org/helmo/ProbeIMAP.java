package org.helmo;


import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.helmo.ImapBuilder.*;

public class ProbeIMAP extends Probe{
    private boolean running;
    private final ScheduledExecutorService scheduler;
    private final Map<Aurl,String> aurlsStatus;
    private int frequency;

    public ProbeIMAP(ConfigProbes configProbes) {
        super(configProbes);
        this.aurlsStatus = new HashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(3);
    }

    @Override
    public void start() {
        running = true;

    }

    @Override
    public void stop() {
        running = false;
        scheduler.shutdown();
    }

    @Override
    protected void collectData() {
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


    private void startThreadLoop(Runnable runnable, long delay) {
        scheduler.scheduleWithFixedDelay(() -> {
            if (running) {
                runnable.run();
            }
        }, 0, delay, TimeUnit.SECONDS);
    }
}
