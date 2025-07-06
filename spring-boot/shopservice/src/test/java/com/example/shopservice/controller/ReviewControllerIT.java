package com.example.shopservice.controller;

import com.example.shopservice.entity.Product;
import com.example.shopservice.entity.User;
import com.example.shopservice.repository.OrderItemRepository;
import com.example.shopservice.repository.OrderRepository;
import com.example.shopservice.repository.ProductRepository;
import com.example.shopservice.repository.ReviewRepository;
import com.example.shopservice.repository.UserRepository;
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
public class ReviewControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
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
        String reviewJson = """
        {
          "user":{"id":%d},
          "product":{"id":%d},
          "rating":5,
          "comment":"Awesome!"
        }
        """.formatted(user.getId(), product.getId());

        String location = mockMvc.perform(post("/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reviewJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.rating").value(5))
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(get("/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())));
    }
}
