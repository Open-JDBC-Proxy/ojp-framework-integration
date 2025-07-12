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

public class ReviewResourceIT {

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
    void testCreateAndGetReview() throws IOException, InterruptedException {
        // Create a user first
        String userJson = "{\"username\":\"reviewer\",\"email\":\"reviewer@example.com\"}";
        HttpRequest createUserRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .build();

        HttpResponse<String> userResponse = client.send(createUserRequest, HttpResponse.BodyHandlers.ofString());
        String userIdStr = userResponse.body().substring(userResponse.body().indexOf("\"id\":") + 5);
        userIdStr = userIdStr.substring(0, userIdStr.indexOf(","));
        Long userId = Long.parseLong(userIdStr);

        // Create a product
        String productJson = "{\"name\":\"Reviewable Product\",\"price\":15.99}";
        HttpRequest createProductRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/products"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(productJson))
                .build();

        HttpResponse<String> productResponse = client.send(createProductRequest, HttpResponse.BodyHandlers.ofString());
        String productIdStr = productResponse.body().substring(productResponse.body().indexOf("\"id\":") + 5);
        productIdStr = productIdStr.substring(0, productIdStr.indexOf(","));
        Long productId = Long.parseLong(productIdStr);

        // Create a review
        String reviewJson = String.format(
            "{\n" +
            "  \"user\": {\"id\": %d},\n" +
            "  \"product\": {\"id\": %d},\n" +
            "  \"rating\": 5,\n" +
            "  \"comment\": \"Excellent product!\"\n" +
            "}", userId, productId);

        HttpRequest createReviewRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/reviews"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(reviewJson))
                .build();

        HttpResponse<String> reviewResponse = client.send(createReviewRequest, HttpResponse.BodyHandlers.ofString());
        
        assertThat(reviewResponse.statusCode(), is(201));
        assertThat(reviewResponse.body(), containsString("Excellent product!"));
        assertThat(reviewResponse.body(), containsString("\"rating\":5"));
        assertThat(reviewResponse.body(), containsString("reviewer"));
        assertThat(reviewResponse.body(), containsString("Reviewable Product"));
    }

    @Test
    void testListReviews() throws IOException, InterruptedException {
        HttpRequest listRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/reviews"))
                .GET()
                .build();

        HttpResponse<String> listResponse = client.send(listRequest, HttpResponse.BodyHandlers.ofString());
        
        assertThat(listResponse.statusCode(), is(200));
        assertThat(listResponse.body(), containsString("content"));
        assertThat(listResponse.body(), containsString("totalElements"));
    }
}