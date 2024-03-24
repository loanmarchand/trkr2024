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
            this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

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

                if (Objects.equals(command.getCommandType(), "STOP")) {
                    running = false;
                    break;
                }else
                if (Objects.equals(command.getCommandType(), "NEWMON")) {
                    handleNewMonCommand(command);
                }else
                if (Objects.equals(command.getCommandType(), "LISTMON")) {
                    handleListMonCommand();
                }else
                if (Objects.equals(command.getCommandType(), "REQUEST")) {
                    handleRequestCommand(command);
                }
                else {
                    System.out.println("Commande non reconnue");
                }
            }
            close();
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture de la commande: " + e.getMessage());
            close();
        }
    }

    private void handleRequestCommand(Command command) {
        ResultState result = tlsServer.getMonitor(command.getId());
        out.println(MessageBuilder.buildRespond(command.getId(),result.url(),result.state()));
    }

    private void handleListMonCommand() {
        System.out.println(MessageBuilder.buildMon(tlsServer.getIdAurl()));
        out.println(MessageBuilder.buildMon(tlsServer.getIdAurl()));
        System.out.println("ok");
    }

    private void handleNewMonCommand(Command command) {
        boolean result = tlsServer.AddMonitor(command);
        if (result) {
            out.println(MessageBuilder.buildNewmonResp("+OK"));
        } else {
            out.println(MessageBuilder.buildNewmonResp("-ERR", "Monitor already exists"));
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
