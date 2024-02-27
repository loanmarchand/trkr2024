package org.helmo;

import java.io.*;
import java.net.*;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.net.Socket;

import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;


public class MonitorDaemon {
    private final ConfigMonitor configMonitor;
    private MulticastSocket socket;

    public MonitorDaemon(ConfigMonitor configMonitor) {
        this.configMonitor = configMonitor;
    }

    public void start() {
        System.out.println("Starting the monitor daemon");
        try {
            InetAddress group = InetAddress.getByName(configMonitor.multicastAdress());
            this.socket = new MulticastSocket(configMonitor.multicastPort());
            NetworkInterface networkInterface = NetworkInterface.getByName(configMonitor.multicastInterface());
            this.socket.joinGroup(new InetSocketAddress(group, configMonitor.multicastPort()), networkInterface);
            listenForMulticast();
        } catch (IOException e) {
            System.out.println("IOException in start: " + e.getMessage());
        }
    }

    private void listenForMulticast() {
        new Thread(() -> {
            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    InetAddress probeAddress = packet.getAddress();
                    System.out.println("Received multicast message: " + message);
                    processMessage(message, probeAddress);
                } catch (IOException e) {
                    System.out.println("IOException in listenForMulticast: " + e.getMessage());
                    break;
                }
            }
        }).start();
    }

    private void processMessage(String message, InetAddress probeAddress) {
        Command command = MessageAnalyzer.analyzeMessage(message);
        if (command != null) {
            System.out.println("Commande reconnue: " + command.getCommandType());
            switch (command.getCommandType()) {
                case "PROBE":
                    List<Aurl> aurls = configMonitor.probes().stream()
                            .filter(aurl -> aurl.type().contains(command.getProtocole()))
                            .toList();
                    sendAurlsToProbes(aurls, command, probeAddress);
                    break;
                case "DATA":
                    // Handle data command
                    break;
                default:
                    System.out.println("Commande non reconnue");
                    break;
            }
        }
    }

    private void sendAurlsToProbes(List<Aurl> aurls, Command command, InetAddress probeAddress) {
        System.out.println(command);
        // Example of sending AURLs to the probe
        try (Socket socket = new Socket(probeAddress, Integer.parseInt(command.getPort()));
             PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true)) {
            String message = MessageBuilder.buildSetup(configMonitor.protocolsDelay().get(command.getProtocole()+'s'), aurls);
            System.out.println("Sending AURLs to probe: " + message);
            out.print(message);
        } catch (IOException e) {
            System.out.println("Error sending AURLs to probe: " + e.getMessage());
        }
    }

    private void TLS(){
        // Chargement du fichier de certificat du serveur
        System.setProperty("javax.net.ssl.keyStore", "../../../ressources/star.labo24.swilabus.com.p12");
        System.setProperty("javax.net.ssl.keyStorePassword", "labo24");

        // Chemin vers les fichiers .crt
        String cheminCertificat1 = "../../../ressources/SwilabusIntermediateG21.crt";
        String cheminCertificat2 = "../../../ressources/SwilabusMainCertificateG1.crt";

        try {
            // Conversion des fichiers .crt en un truststore temporaire
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null); // Initialisation du truststore

            // Charger le premier certificat
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream certStream1 = new FileInputStream(cheminCertificat1);
            Certificate cert1 = cf.generateCertificate(certStream1);
            trustStore.setCertificateEntry("cert1", cert1);

            // Charger le deuxième certificat
            InputStream certStream2 = new FileInputStream(cheminCertificat2);
            Certificate cert2 = cf.generateCertificate(certStream2);
            trustStore.setCertificateEntry("cert2", cert2);

            // Enregistrer le truststore temporaire sur le disque
            String cheminTruststore = "./../../ressources/truststore";
            trustStore.store(new FileOutputStream(cheminTruststore), "labo24".toCharArray());

            // Configurer Java pour utiliser le truststore nouvellement créé
            System.setProperty("javax.net.ssl.trustStore", cheminTruststore);
            System.setProperty("javax.net.ssl.trustStorePassword", "labo24");

        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            System.out.println(e.getMessage());
        }

        // Configuration des protocoles et des suites de chiffrement
        System.setProperty("https.protocols", "TLSv1.3");
        System.setProperty("https.cipherSuites", "TLS_AES_128_GCM_SHA256");

        // Utilisation des sockets sécurisés
        try {
            SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(0000);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        // Acceptation des connexions entrantes et gestion des communications sécurisées

    }

    public static void main(String[] args) {
        JsonHelper reader = new JsonHelper();
        ConfigMonitor configMonitor = reader.readConfigMonitor("json/src/main/resources/config-monitor.json");
        MonitorDaemon monitorDaemon = new MonitorDaemon(configMonitor);
        monitorDaemon.start();
    }
}
