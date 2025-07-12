package com.example.shopservice.controller;

import com.example.shopservice.entity.Order;
import com.example.shopservice.entity.OrderItem;
import com.example.shopservice.entity.Product;
import com.example.shopservice.entity.User;
import com.example.shopservice.repository.OrderRepository;
import com.example.shopservice.repository.ProductRepository;
import com.example.shopservice.repository.UserRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@Controller("/orders")
public class OrderController {
    @Inject
    private OrderRepository orderRepository;
    @Inject
    private UserRepository userRepository;
    @Inject
    private ProductRepository productRepository;

    @Post
    public HttpResponse<Order> create(@Body Order order) {
        if (order.getUser() == null || order.getUser().getId() == null) {
            return HttpResponse.badRequest();
        }
        Optional<User> userOpt = userRepository.findById(order.getUser().getId());
        if (userOpt.isEmpty()) return HttpResponse.badRequest();

        order.setUser(userOpt.get());

        List<OrderItem> items = order.getOrderItems();
        if (items != null) {
            for (OrderItem item : items) {
                if (item.getProduct() == null || item.getProduct().getId() == null)
                    return HttpResponse.badRequest();
                Optional<Product> prodOpt = productRepository.findById(item.getProduct().getId());
                if (prodOpt.isEmpty()) return HttpResponse.badRequest();
                item.setProduct(prodOpt.get());
                item.setOrder(order);
            }
        }
        return HttpResponse.ok(orderRepository.save(order));
    }

    @Get
    public Page<Order> list(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Get("/{id}")
    public HttpResponse<Order> get(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }

    @Put("/{id}")
    public HttpResponse<Order> update(@PathVariable Long id, @Body Order order) {
        return orderRepository.findById(id)
                .map(existing -> {
                    if (order.getUser() != null && order.getUser().getId() != null) {
                        Optional<User> userOpt = userRepository.findById(order.getUser().getId());
                        userOpt.ifPresent(existing::setUser);
                    }
                    if (order.getOrderDate() != null) {
                        existing.setOrderDate(order.getOrderDate());
                    }
                    return HttpResponse.ok(orderRepository.save(existing));
                })
                .orElse(HttpResponse.notFound());
    }

    @Delete("/{id}")
    public HttpResponse<Object> delete(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(existing -> {
                    orderRepository.delete(existing);
                    return HttpResponse.noContent();
                })
                .orElse(HttpResponse.notFound());
    }
}