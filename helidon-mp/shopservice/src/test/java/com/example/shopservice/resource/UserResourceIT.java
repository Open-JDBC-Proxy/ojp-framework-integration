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

public class UserResourceIT {

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
    void testCreateAndGetUser() throws IOException, InterruptedException {
        String userJson = "{\"username\":\"alice\",\"email\":\"alice@example.com\"}";
        
        // Create user
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .build();

        HttpResponse<String> createResponse = client.send(createRequest, HttpResponse.BodyHandlers.ofString());
        
        assertThat(createResponse.statusCode(), is(201));
        assertThat(createResponse.body(), containsString("alice"));
        assertThat(createResponse.body(), containsString("alice@example.com"));
        
        // Extract user ID from response (simple approach)
        String responseBody = createResponse.body();
        String idStr = responseBody.substring(responseBody.indexOf("\"id\":") + 5);
        idStr = idStr.substring(0, idStr.indexOf(","));
        Long userId = Long.parseLong(idStr);
        
        // Get user by ID
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/users/" + userId))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        
        assertThat(getResponse.statusCode(), is(200));
        assertThat(getResponse.body(), containsString("alice@example.com"));
    }

    @Test
    void testListUsers() throws IOException, InterruptedException {
        // Get list of users
        HttpRequest listRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/users"))
                .GET()
                .build();

        HttpResponse<String> listResponse = client.send(listRequest, HttpResponse.BodyHandlers.ofString());
        
        assertThat(listResponse.statusCode(), is(200));
        assertThat(listResponse.body(), containsString("content"));
        assertThat(listResponse.body(), containsString("totalElements"));
    }

    @Test
    void testGetNonExistentUser() throws IOException, InterruptedException {
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/users/99999"))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        
        assertThat(getResponse.statusCode(), is(404));
    }
}