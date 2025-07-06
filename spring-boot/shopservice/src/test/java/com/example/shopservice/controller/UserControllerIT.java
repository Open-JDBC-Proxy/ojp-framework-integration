package com.example.shopservice.controller;

import com.example.shopservice.entity.User;
import com.example.shopservice.repository.OrderItemRepository;
import com.example.shopservice.repository.OrderRepository;
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
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
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
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("alice"));

        User user = userRepository.findAll().get(0);
        mockMvc.perform(get("/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }
}
