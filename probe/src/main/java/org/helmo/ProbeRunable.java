package org.helmo;

import java.net.Socket;

public interface ProbeRunable extends Runnable {
    void updateProbe(Socket socket);
}
