package com.example.shopservice.controller;

import com.example.shopservice.entity.User;
import com.example.shopservice.repository.OrderItemRepository;
import com.example.shopservice.repository.OrderRepository;
import com.example.shopservice.repository.ReviewRepository;
import com.example.shopservice.repository.UserRepository;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class UserControllerIT {
    @Inject
    @Client("/")
    HttpClient client;
    
    @Inject
    private UserRepository userRepository;
    @Inject
    private OrderItemRepository orderItemRepository;
    @Inject
    private OrderRepository orderRepository;
    @Inject
    private ReviewRepository reviewRepository;

    @BeforeEach
    void setup() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        reviewRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testCreateAndGetUser() throws Exception {
        String json = "{\"username\":\"alice\",\"email\":\"alice@example.com\"}";
        var response = client.toBlocking().exchange(
            HttpRequest.POST("/users", json),
            User.class
        );
        
        assertEquals(HttpStatus.OK, response.status());
        User created = response.body();
        assertNotNull(created.getId());
        assertEquals("alice", created.getUsername());

        var getResponse = client.toBlocking().exchange(
            HttpRequest.GET("/users/" + created.getId()),
            User.class
        );
        
        assertEquals(HttpStatus.OK, getResponse.status());
        User retrieved = getResponse.body();
        assertEquals("alice@example.com", retrieved.getEmail());
    }
}