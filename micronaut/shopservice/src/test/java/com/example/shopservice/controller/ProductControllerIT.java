package com.example.shopservice.controller;

import com.example.shopservice.entity.Product;
import com.example.shopservice.repository.OrderItemRepository;
import com.example.shopservice.repository.OrderRepository;
import com.example.shopservice.repository.ProductRepository;
import com.example.shopservice.repository.ReviewRepository;
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
    void testCreateProduct() throws Exception {
        String json = "{\"name\":\"Widget\",\"price\":19.99}";
        var response = client.toBlocking().exchange(
            HttpRequest.POST("/products", json)
                .contentType(MediaType.APPLICATION_JSON),
            Product.class
        );
        
        assertEquals(HttpStatus.OK, response.status());
        Product created = response.body();
        assertNotNull(created.getId());
        assertEquals("Widget", created.getName());
        assertEquals(new BigDecimal("19.99"), created.getPrice());
    }

    @Test
    void testGetProduct() throws Exception {
        String json = "{\"name\":\"Gadget\",\"price\":10.50}";
        var response = client.toBlocking().exchange(
            HttpRequest.POST("/products", json)
                .contentType(MediaType.APPLICATION_JSON),
            Product.class
        );
        
        Long id = response.body().getId();

        var getResponse = client.toBlocking().exchange(
            HttpRequest.GET("/products/" + id),
            Product.class
        );
        
        assertEquals(HttpStatus.OK, getResponse.status());
        Product retrieved = getResponse.body();
        assertEquals("Gadget", retrieved.getName());
        assertEquals(new BigDecimal("10.50"), retrieved.getPrice());
    }

    @Test
    void testUpdateProduct() throws Exception {
        // First create a product
        String json = "{\"name\":\"Thing\",\"price\":5.00}";
        var response = client.toBlocking().exchange(
            HttpRequest.POST("/products", json)
                .contentType(MediaType.APPLICATION_JSON),
            Product.class
        );
        
        Long id = response.body().getId();

        // Use JSON for update like other tests
        String updateJson = "{\"name\":\"Thing Updated\",\"price\":7.50}";
        var updateResponse = client.toBlocking().exchange(
            HttpRequest.PUT("/products/" + id, updateJson)
                .contentType(MediaType.APPLICATION_JSON),
            Product.class
        );
        
        assertEquals(HttpStatus.OK, updateResponse.status());
        Product updated = updateResponse.body();
        assertEquals("Thing Updated", updated.getName());
        assertEquals(new BigDecimal("7.50"), updated.getPrice());
    }

    @Test
    void testDeleteProduct() throws Exception {
        String json = "{\"name\":\"ToDelete\",\"price\":1.00}";
        var response = client.toBlocking().exchange(
            HttpRequest.POST("/products", json)
                .contentType(MediaType.APPLICATION_JSON),
            Product.class
        );
        
        Long id = response.body().getId();

        var deleteResponse = client.toBlocking().exchange(
            HttpRequest.DELETE("/products/" + id)
        );
        
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.status());

        // Use retrieveResponse to handle 404 without exception
        try {
            client.toBlocking().exchange(
                HttpRequest.GET("/products/" + id),
                Product.class
            );
            fail("Expected 404 Not Found");
        } catch (io.micronaut.http.client.exceptions.HttpClientResponseException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        }
    }
}