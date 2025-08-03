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
    void testCreateReview() throws Exception {
        String reviewJson = String.format("""
        {
          "user":{"id":%d},
          "product":{"id":%d},
          "rating":5,
          "comment":"Excellent!"
        }
        """, user.getId(), product.getId());

        var response = client.toBlocking().exchange(
            HttpRequest.POST("/reviews", reviewJson)
                .contentType(MediaType.APPLICATION_JSON),
            String.class
        );
        
        assertEquals(HttpStatus.OK, response.status());
        assertTrue(response.body().contains("\"rating\":5"));
        assertTrue(response.body().contains("\"comment\":\"Excellent!\""));
    }

    @Test
    void testGetReview() throws Exception {
        String reviewJson = String.format("""
        {
          "user":{"id":%d},
          "product":{"id":%d},
          "rating":4,
          "comment":"Good!"
        }
        """, user.getId(), product.getId());

        var createResponse = client.toBlocking().exchange(
            HttpRequest.POST("/reviews", reviewJson)
                .contentType(MediaType.APPLICATION_JSON),
            String.class
        );
        
        // Extract review ID from response
        String responseBody = createResponse.body();
        Long reviewId = Long.parseLong(responseBody.substring(responseBody.indexOf("\"id\":") + 5, responseBody.indexOf(",", responseBody.indexOf("\"id\":"))));

        var getResponse = client.toBlocking().exchange(
            HttpRequest.GET("/reviews/" + reviewId),
            String.class
        );
        
        assertEquals(HttpStatus.OK, getResponse.status());
        assertTrue(getResponse.body().contains("\"rating\":4"));
        assertTrue(getResponse.body().contains("\"comment\":\"Good!\""));
    }

    @Test
    void testDeleteReview() throws Exception {
        String reviewJson = String.format("""
        {
          "user":{"id":%d},
          "product":{"id":%d},
          "rating":2,
          "comment":"Not great."
        }
        """, user.getId(), product.getId());

        var createResponse = client.toBlocking().exchange(
            HttpRequest.POST("/reviews", reviewJson)
                .contentType(MediaType.APPLICATION_JSON),
            String.class
        );
        
        // Extract review ID from response
        String responseBody = createResponse.body();
        Long reviewId = Long.parseLong(responseBody.substring(responseBody.indexOf("\"id\":") + 5, responseBody.indexOf(",", responseBody.indexOf("\"id\":"))));

        var deleteResponse = client.toBlocking().exchange(
            HttpRequest.DELETE("/reviews/" + reviewId)
        );
        
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.status());

        // Use exception handling for 404 check
        try {
            client.toBlocking().exchange(
                HttpRequest.GET("/reviews/" + reviewId),
                String.class
            );
            fail("Expected 404 Not Found");
        } catch (io.micronaut.http.client.exceptions.HttpClientResponseException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        }
    }
}