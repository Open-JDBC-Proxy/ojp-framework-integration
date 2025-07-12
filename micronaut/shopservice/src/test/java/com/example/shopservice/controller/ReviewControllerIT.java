package com.example.shopservice.controller;

import com.example.shopservice.entity.Product;
import com.example.shopservice.entity.User;
import com.example.shopservice.repository.OrderItemRepository;
import com.example.shopservice.repository.OrderRepository;
import com.example.shopservice.repository.ProductRepository;
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

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class ReviewControllerIT {
    @Inject
    @Client("/")
    HttpClient client;
    
    @Inject
    private UserRepository userRepository;
    @Inject
    private ProductRepository productRepository;
    @Inject
    private OrderItemRepository orderItemRepository;
    @Inject
    private OrderRepository orderRepository;
    @Inject
    private ReviewRepository reviewRepository;

    private User user;
    private Product product;

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
        
        Product productObj = new Product();
        productObj.setName("SuperToy");
        productObj.setPrice(new BigDecimal("99.99"));
        product = productRepository.save(productObj);
    }

    @Test
    void testCreateAndGetReview() throws Exception {
        String reviewJson = String.format("""
        {
          "user":{"id":%d},
          "product":{"id":%d},
          "rating":5,
          "comment":"Awesome!"
        }
        """, user.getId(), product.getId());

        var response = client.toBlocking().exchange(
            HttpRequest.POST("/reviews", reviewJson),
            String.class
        );
        
        assertEquals(HttpStatus.OK, response.status());

        var listResponse = client.toBlocking().exchange(
            HttpRequest.GET("/reviews"),
            String.class
        );
        
        assertEquals(HttpStatus.OK, listResponse.status());
        assertTrue(listResponse.body().contains("content"));
    }
}