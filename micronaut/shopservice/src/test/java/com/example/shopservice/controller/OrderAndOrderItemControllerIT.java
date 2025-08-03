package com.example.shopservice.controller;

import com.example.shopservice.entity.Order;
import com.example.shopservice.entity.Product;
import com.example.shopservice.entity.User;
import com.example.shopservice.repository.OrderItemRepository;
import com.example.shopservice.repository.OrderRepository;
import com.example.shopservice.repository.ProductRepository;
import com.example.shopservice.repository.ReviewRepository;
import com.example.shopservice.repository.UserRepository;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class OrderAndOrderItemControllerIT {
    @Inject
    @Client("/")
    HttpClient client;
    
    @Inject
    private UserRepository userRepository;
    @Inject
    private ProductRepository productRepository;
    @Inject
    private OrderRepository orderRepository;
    @Inject
    private OrderItemRepository orderItemRepository;
    @Inject
    private ReviewRepository reviewRepository;

    private User user;
    private Product product1, product2;

    @BeforeEach
    void setup() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        reviewRepository.deleteAll();
        userRepository.deleteAll();
        productRepository.deleteAll();
        
        User userObj = new User();
        userObj.setUsername("bob");
        userObj.setEmail("bob@example.com");
        user = userRepository.save(userObj);

        Product prod1 = new Product();
        prod1.setName("Thing");
        prod1.setPrice(new BigDecimal("10.00"));
        product1 = productRepository.save(prod1);

        Product prod2 = new Product();
        prod2.setName("Gadget");
        prod2.setPrice(new BigDecimal("5.50"));
        product2 = productRepository.save(prod2);
    }

    @Test
    void testCreateOrder() throws Exception {
        String simpleOrderJson = String.format("""
        {
          "user":{"id":%d}
        }
        """, user.getId());

        var response = client.toBlocking().exchange(
            HttpRequest.POST("/orders", simpleOrderJson)
                .contentType(MediaType.APPLICATION_JSON),
            Order.class
        );
        
        assertEquals(HttpStatus.OK, response.status());
        Order created = response.body();
        assertNotNull(created.getId());
        assertEquals(user.getId(), created.getUser().getId());
    }

    @Test
    void testGetOrder() throws Exception {
        String simpleOrderJson = String.format("""
        {
          "user":{"id":%d}
        }
        """, user.getId());

        var createResponse = client.toBlocking().exchange(
            HttpRequest.POST("/orders", simpleOrderJson)
                .contentType(MediaType.APPLICATION_JSON),
            Order.class
        );
        
        Long orderId = createResponse.body().getId();

        var getResponse = client.toBlocking().exchange(
            HttpRequest.GET("/orders/" + orderId),
            Order.class
        );
        
        assertEquals(HttpStatus.OK, getResponse.status());
        Order retrieved = getResponse.body();
        assertEquals(user.getId(), retrieved.getUser().getId());
    }

    @Test
    void testDeleteOrder() throws Exception {
        String simpleOrderJson = String.format("""
        {
          "user":{"id":%d}
        }
        """, user.getId());

        var createResponse = client.toBlocking().exchange(
            HttpRequest.POST("/orders", simpleOrderJson)
                .contentType(MediaType.APPLICATION_JSON),
            Order.class
        );
        
        Long orderId = createResponse.body().getId();

        var deleteResponse = client.toBlocking().exchange(
            HttpRequest.DELETE("/orders/" + orderId)
        );
        
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.status());

        // Use exception handling for 404 check
        try {
            client.toBlocking().exchange(
                HttpRequest.GET("/orders/" + orderId),
                Order.class
            );
            fail("Expected 404 Not Found");
        } catch (io.micronaut.http.client.exceptions.HttpClientResponseException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        }
    }
}