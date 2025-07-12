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

public class OrderAndOrderItemResourceIT {

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
    void testCreateOrderWithItems() throws IOException, InterruptedException {
        // First create a user
        String userJson = "{\"username\":\"john\",\"email\":\"john@example.com\"}";
        HttpRequest createUserRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .build();

        HttpResponse<String> userResponse = client.send(createUserRequest, HttpResponse.BodyHandlers.ofString());
        assertThat(userResponse.statusCode(), is(201));
        
        // Extract user ID
        String userResponseBody = userResponse.body();
        String userIdStr = userResponseBody.substring(userResponseBody.indexOf("\"id\":") + 5);
        userIdStr = userIdStr.substring(0, userIdStr.indexOf(","));
        Long userId = Long.parseLong(userIdStr);

        // Create a product
        String productJson = "{\"name\":\"Widget\",\"price\":25.00}";
        HttpRequest createProductRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/products"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(productJson))
                .build();

        HttpResponse<String> productResponse = client.send(createProductRequest, HttpResponse.BodyHandlers.ofString());
        assertThat(productResponse.statusCode(), is(201));
        
        // Extract product ID
        String productResponseBody = productResponse.body();
        String productIdStr = productResponseBody.substring(productResponseBody.indexOf("\"id\":") + 5);
        productIdStr = productIdStr.substring(0, productIdStr.indexOf(","));
        Long productId = Long.parseLong(productIdStr);

        // Create an order with order items
        String orderJson = String.format(
            "{\n" +
            "  \"user\": {\"id\": %d},\n" +
            "  \"orderItems\": [\n" +
            "    {\n" +
            "      \"product\": {\"id\": %d},\n" +
            "      \"quantity\": 2\n" +
            "    }\n" +
            "  ]\n" +
            "}", userId, productId);

        HttpRequest createOrderRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/orders"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(orderJson))
                .build();

        HttpResponse<String> orderResponse = client.send(createOrderRequest, HttpResponse.BodyHandlers.ofString());
        
        assertThat(orderResponse.statusCode(), is(201));
        assertThat(orderResponse.body(), containsString("john"));
        assertThat(orderResponse.body(), containsString("Widget"));
        assertThat(orderResponse.body(), containsString("\"quantity\":2"));
    }

    @Test
    void testCreateOrderWithInvalidUser() throws IOException, InterruptedException {
        String orderJson = "{\"user\":{\"id\":99999},\"orderItems\":[]}";
        
        HttpRequest createOrderRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/orders"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(orderJson))
                .build();

        HttpResponse<String> orderResponse = client.send(createOrderRequest, HttpResponse.BodyHandlers.ofString());
        
        assertThat(orderResponse.statusCode(), is(400));
    }

    @Test
    void testListOrders() throws IOException, InterruptedException {
        HttpRequest listRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/orders"))
                .GET()
                .build();

        HttpResponse<String> listResponse = client.send(listRequest, HttpResponse.BodyHandlers.ofString());
        
        assertThat(listResponse.statusCode(), is(200));
        assertThat(listResponse.body(), containsString("content"));
        assertThat(listResponse.body(), containsString("totalElements"));
    }
}