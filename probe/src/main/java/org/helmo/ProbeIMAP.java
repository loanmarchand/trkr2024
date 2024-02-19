package org.helmo;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProbeIMAP extends Probe{
    private boolean running;
    private final ScheduledExecutorService scheduler;
    private final ConfigMonitor configMonitor;
    public ProbeIMAP(ConfigProbes configProbes,ConfigMonitor configMonitor) {
        super(configProbes);
        this.configMonitor = configMonitor;
        this.scheduler = Executors.newScheduledThreadPool(3);
    }

    @Override
    public void start() {
        running = true;
        startThreadLoop(this::collectData, Long.parseLong(configMonitor.protocolsDelay().get("imap")));
    }

    @Override
    public void stop() {

    }

    @Override
    protected void collectData() {
        for (Aurl aurl : configMonitor.probes()) {
            if (aurl.type().contains("imap")) {
                collectData(aurl);
            }
        }
    }

    private void collectData(Aurl aurl) {

    }

    private void startThreadLoop(Runnable runnable, long delay) {
        scheduler.scheduleWithFixedDelay(() -> {
            if (running) {
                runnable.run();
            }
        }, 0, delay, TimeUnit.SECONDS);
    }
}
