package com.example.shopservice.resource;

import io.helidon.microprofile.server.Server;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ProductResourceIT {

    private static Server server;
    private static String baseUrl;
    private static HttpClient client;

    @BeforeAll
    static void startServer() throws InterruptedException {
        server = Server.create();
        server.start();
        
        baseUrl = "http://localhost:" + server.port();
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        
        // Wait a bit for server to be ready
        Thread.sleep(2000);
    }

    @AfterAll
    static void stopServer() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    void testCreateAndGetProduct() throws IOException, InterruptedException {
        String productJson = "{\"name\":\"Test Product\",\"price\":19.99}";
        
        // Create product
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/products"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(productJson))
                .build();

        HttpResponse<String> createResponse = client.send(createRequest, HttpResponse.BodyHandlers.ofString());
        
        assertThat(createResponse.statusCode(), is(201));
        assertThat(createResponse.body(), containsString("Test Product"));
        assertThat(createResponse.body(), containsString("19.99"));
        
        // Extract product ID from response
        String responseBody = createResponse.body();
        String idStr = responseBody.substring(responseBody.indexOf("\"id\":") + 5);
        idStr = idStr.substring(0, idStr.indexOf(","));
        Long productId = Long.parseLong(idStr);
        
        // Get product by ID
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/products/" + productId))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        
        assertThat(getResponse.statusCode(), is(200));
        assertThat(getResponse.body(), containsString("Test Product"));
        assertThat(getResponse.body(), containsString("19.99"));
    }

    @Test
    void testListProducts() throws IOException, InterruptedException {
        // Get list of products
        HttpRequest listRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/products"))
                .GET()
                .build();

        HttpResponse<String> listResponse = client.send(listRequest, HttpResponse.BodyHandlers.ofString());
        
        assertThat(listResponse.statusCode(), is(200));
        assertThat(listResponse.body(), containsString("content"));
        assertThat(listResponse.body(), containsString("totalElements"));
    }
}