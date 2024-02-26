package org.helmo;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Scanner;

public class NetworkInterfaceSelector {
    public static void main(String[] args) {
        Enumeration<NetworkInterface> interfaces;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();

            System.out.println("Interfaces réseau disponibles :");
            int index = 1;
            for (NetworkInterface ni : Collections.list(interfaces)) {
                System.out.println(index++ + " - " + ni.getName());
            }


            //Stocker le nom de l'interface sélectionnée dans config-monitor.json

        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }
}
