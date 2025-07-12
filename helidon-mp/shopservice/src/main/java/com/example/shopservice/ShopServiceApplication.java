package com.example.shopservice;

import io.helidon.microprofile.server.Server;

/**
 * Main class for the Helidon MP Shop Service application.
 */
public class ShopServiceApplication {

    public static void main(String[] args) {
        Server server = Server.create();
        server.start();
        
        System.out.printf("Helidon MP server started on http://localhost:%d%n", server.port());
    }
}