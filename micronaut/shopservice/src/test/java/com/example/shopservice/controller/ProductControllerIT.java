package com.example.shopservice.controller;

import com.example.shopservice.entity.Product;
import com.example.shopservice.repository.OrderItemRepository;
import com.example.shopservice.repository.OrderRepository;
import com.example.shopservice.repository.ProductRepository;
import com.example.shopservice.repository.ReviewRepository;
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
public class ProductControllerIT {
    @Inject
    @Client("/")
    HttpClient client;
    
    @Inject
    private OrderItemRepository orderItemRepository;
    @Inject
    private OrderRepository orderRepository;
    @Inject
    private ReviewRepository reviewRepository;
    @Inject
    private ProductRepository productRepository;

    @BeforeEach
    void setup() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        reviewRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void testCreateAndGetProduct() throws Exception {
        String json = "{\"name\":\"Widget\",\"price\":19.99}";
        var response = client.toBlocking().exchange(
            HttpRequest.POST("/products", json),
            Product.class
        );
        
        assertEquals(HttpStatus.OK, response.status());
        Product created = response.body();
        assertNotNull(created.getId());
        assertEquals("Widget", created.getName());

        var getResponse = client.toBlocking().exchange(
            HttpRequest.GET("/products/" + created.getId()),
            Product.class
        );
        
        assertEquals(HttpStatus.OK, getResponse.status());
        Product retrieved = getResponse.body();
        assertEquals(new BigDecimal("19.99"), retrieved.getPrice());
    }
}