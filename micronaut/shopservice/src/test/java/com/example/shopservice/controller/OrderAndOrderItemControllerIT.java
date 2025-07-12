package com.example.shopservice.controller;

import com.example.shopservice.entity.Order;
import com.example.shopservice.entity.Product;
import com.example.shopservice.entity.User;
import com.example.shopservice.repository.OrderRepository;
import com.example.shopservice.repository.ProductRepository;
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

    private User user;
    private Product product1, product2;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        productRepository.deleteAll();
        orderRepository.deleteAll();
        
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
    void createOrderWithItemsAndGetItems() throws Exception {
        String orderJson = String.format("""
        {
          "user":{"id":%d},
          "orderItems":[
            {"product":{"id":%d},"quantity":2},
            {"product":{"id":%d},"quantity":1}
          ]
        }
        """, user.getId(), product1.getId(), product2.getId());

        var response = client.toBlocking().exchange(
            HttpRequest.POST("/orders", orderJson),
            Order.class
        );
        
        assertEquals(HttpStatus.OK, response.status());
        Order order = response.body();
        assertNotNull(order.getId());

        var getResponse = client.toBlocking().exchange(
            HttpRequest.GET("/orders/" + order.getId()),
            String.class
        );
        
        assertEquals(HttpStatus.OK, getResponse.status());
        assertTrue(getResponse.body().contains("orderItems"));

        var itemsResponse = client.toBlocking().exchange(
            HttpRequest.GET("/orders/" + order.getId() + "/items"),
            String.class
        );
        
        assertEquals(HttpStatus.OK, itemsResponse.status());
    }
}