package com.cabbooking;

import com.cabbooking.server.CabBookingServer;
import java.awt.Desktop;
import java.net.URI;

public class Main {
    public static void main(String[] args) {
        // Start the Web Server
        CabBookingServer.startServer();

        // Automatically open default web browser
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(new URI("http://localhost:8080"));
                    System.out.println("🚀 Launched default web browser to: http://localhost:8080");
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Could not launch browser automatically. Please open http://localhost:8080 in your browser manually.");
        }
    }
}

