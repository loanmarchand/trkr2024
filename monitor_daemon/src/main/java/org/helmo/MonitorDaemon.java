package org.helmo;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MonitorDaemon {
    private ConfigMonitor configMonitor;
    private final AesEncryption aesEncryption;
    private final Map<Aurl, String> aurlStatus;
    private final BlockingQueue<Runnable> worker;
    private final ExecutorService executor;
    private final TlsServer tlsServer;
    private final MulticastListenner multicastListenner;
    JsonHelper reader = new JsonHelper();


    public MonitorDaemon(ConfigMonitor configMonitor) {
        this.configMonitor = configMonitor;
        this.aesEncryption = new AesEncryption();
        this.aurlStatus = new HashMap<>();
        this.worker = new LinkedBlockingQueue<>();
        this.executor = new ThreadPoolExecutor(10,50,60, TimeUnit.SECONDS,worker);
        configMonitor.probes().forEach(aurl -> aurlStatus.put(aurl, "UNKNOWN"));
        tlsServer = new TlsServer(configMonitor.clientPort(), this);
        multicastListenner = new MulticastListenner(worker,this);
    }

    public void start() {
        System.out.println("Starting the monitor daemon");
        executor.execute(()->{
            while (true){
                try {
                    worker.take().run();
                } catch (InterruptedException e) {
                    System.out.println("Error while taking a task from the worker: " + e.getMessage());
                }
            }
        });
        new Thread(multicastListenner).start();
        tlsServer.Run();

    }

    private void updateAurlStatus(String message) {
        Command command = MessageAnalyzer.analyzeMessage(message);
        if (command != null) {
            System.out.println("Commande reconnue: " + command.getCommandType());
            if (command.getCommandType().equals("STATUS")) {
                String id = command.getId();
                String state = command.getState();
                System.out.println(state);
                aurlStatus.keySet().stream()
                        .filter(a -> a.type().equals(id))
                        .findFirst().ifPresent(aurl -> aurlStatus.put(aurl, state));
            } else {
                System.out.println("Commande non reconnue");
            }
        }

    }


    public void sendStatusOfAurl(Aurl aurl, Command command, InetAddress probeAddress) {
        try (Socket socket = new Socket(probeAddress, Integer.parseInt(command.getPort()))) {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message = MessageBuilder.buildStatusof(aurl.type());
            message = aesEncryption.encrypt(message, configMonitor.aesKey());
            out.println(message);
            out.flush();
            // Attente de la réponse STATUS
            String encryptedResponse = in.readLine();
            if (encryptedResponse != null) {
                String response = aesEncryption.decrypt(encryptedResponse, configMonitor.aesKey());
                updateAurlStatus(response);
            } else {
                System.out.println("No response received from probe");
            }
        } catch (IOException e) {
            System.out.println("Error communicating with probe at address " + probeAddress.getHostAddress() + ": " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void sendAurlsToProbes(List<Aurl> aurls, Command command, InetAddress probeAddress) {
        System.out.println(command);
        // Example of sending AURLs to the probe
        try (Socket socket = new Socket(probeAddress, Integer.parseInt(command.getPort())); PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true)) {
            configMonitor = reader.readConfigMonitor("json/src/main/resources/config-monitor.json");
            System.out.println(aurls.size());
            String message = MessageBuilder.buildSetup(configMonitor.protocolsDelay().get(command.getProtocole()), aurls);
            System.out.println("Sending AURLs to probe: " + message);
            message = aesEncryption.encrypt(message, configMonitor.aesKey());
            out.print(message);
        } catch (IOException e) {
            System.out.println("Error sending AURLs to probe: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        JsonHelper reader = new JsonHelper();
        ConfigMonitor configMonitor = reader.readConfigMonitor("json/src/main/resources/config-monitor.json");
        MonitorDaemon monitorDaemon = new MonitorDaemon(configMonitor);
        monitorDaemon.start();
    }

    public boolean addMonitor(Aurl aurl) {
        if (!aurlStatus.containsKey(aurl)) {
            aurlStatus.put(aurl, "UNKNOWN");
            reader.addProbe("json/src/main/resources/config-monitor.json",aurl.type(),RegexBuilder.buildAurl(aurl));
            return true;
        }

        return false;
    }

    public List<String> getIdAurls() {
        return aurlStatus.keySet().stream().map(Aurl::type).toList();
    }

    public ResultState getMonitor(String id) {
        Aurl aurl = aurlStatus.keySet().stream().filter(a -> a.type().equals(id)).findFirst().orElse(null);
        if (aurl == null) {
            return null;
        }
        return new ResultState(aurl.url(), aurlStatus.get(aurl));
    }
}
