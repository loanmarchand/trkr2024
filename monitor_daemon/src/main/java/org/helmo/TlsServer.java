package org.helmo;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

public class TlsServer {
    private final int port;
    private SSLServerSocket serverSocket;
    private final MonitorDaemon monitorDaemon;
    public TlsServer(int port, MonitorDaemon monitorDaemon) {
        this.port = port;
        this.monitorDaemon = monitorDaemon;
    }

    public void Run(){
        try {
            // Chargement du fichier contenant le keystore
            char[] password = "labo24".toCharArray(); // Mot de passe du keystore
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileInputStream fis = new FileInputStream("monitor_daemon/src/main/resources/star.labo24.swilabus.com.p12");
            keyStore.load(fis, password);

            // Initialisation du gestionnaire de clés
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, password);

            // Initialisation du gestionnaire de confiance
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            // Création du contexte SSL
            SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            // Création du serveur Socket
            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);
            System.out.println("Serveur en attente de connexion sur le port " + port + "...");

            while (true){
                handleConnection();
            }

        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException |
                 UnrecoverableKeyException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    private void handleConnection() throws IOException {
        // Attente de connexion
        SSLSocket socket = (SSLSocket) serverSocket.accept();
        System.out.println("Nouvelle connexion: " + socket.getInetAddress().getHostAddress());
        // Création d'un thread pour gérer la connexion
        Thread thread = new Thread(new ConnectionClientHandler(socket,this));
        thread.start();

    }

    public boolean AddMonitor(Command command) {
        return monitorDaemon.addMonitor(command.getAurl());
    }

    public List<String> getIdAurl() {
        return monitorDaemon.getIdAurls();
    }

    public ResultState getMonitor(String id) {
        return monitorDaemon.getMonitor(id);
    }
}
