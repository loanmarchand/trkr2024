package org.helmo;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.util.Objects;

public class ConnectionClientHandler implements Runnable {
    private BufferedReader in;
    private PrintWriter out;
    private final TlsServer tlsServer;
    private boolean running = true;
    public ConnectionClientHandler(SSLSocket socket, TlsServer tlsServer) {
        this.tlsServer = tlsServer;
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
            while (running) {
                System.out.println("Attente d'une commande...");
                String configLine;
                configLine = in.readLine();
                System.out.println("Commande reçue: " + configLine);
                Command command = MessageAnalyzer.analyzeMessage(configLine);

                assert command != null;
                if (Objects.equals(command.getCommandType(), "STOP")) {
                    running = false;
                    break;
                }
            }
            close();
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture de la commande: " + e.getMessage());
            close();
        }
    }

    private void close() {
        try {
            in.close();
            out.close();
            System.out.println("Client disconnected");
        } catch (IOException e) {
            System.out.println("Erreur lors de la fermeture du BufferedReader et du PrintWriter: " + e.getMessage());
        }
    }
}
