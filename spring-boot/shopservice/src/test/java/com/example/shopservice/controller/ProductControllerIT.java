package com.example.shopservice.controller;

import com.example.shopservice.entity.Product;
import com.example.shopservice.repository.OrderItemRepository;
import com.example.shopservice.repository.OrderRepository;
import com.example.shopservice.repository.ProductRepository;
import com.example.shopservice.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
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
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Widget"));

        Product prod = productRepository.findAll().get(0);
        mockMvc.perform(get("/products/" + prod.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(19.99));
    }
}
