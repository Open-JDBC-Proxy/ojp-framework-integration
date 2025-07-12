package com.example.shopservice.controller;

import com.example.shopservice.entity.Order;
import com.example.shopservice.entity.OrderItem;
import com.example.shopservice.entity.Product;
import com.example.shopservice.repository.OrderItemRepository;
import com.example.shopservice.repository.OrderRepository;
import com.example.shopservice.repository.ProductRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

@Controller("/orders/{orderId}/items")
public class OrderItemController {
    @Inject
    private OrderItemRepository orderItemRepository;
    @Inject
    private OrderRepository orderRepository;
    @Inject
    private ProductRepository productRepository;

    @Post
    public HttpResponse<OrderItem> create(@PathVariable Long orderId, @Body OrderItem orderItem) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) return HttpResponse.badRequest();
        if (orderItem.getProduct() == null || orderItem.getProduct().getId() == null)
            return HttpResponse.badRequest();
        Optional<Product> prodOpt = productRepository.findById(orderItem.getProduct().getId());
        if (prodOpt.isEmpty()) return HttpResponse.badRequest();

        orderItem.setOrder(orderOpt.get());
        orderItem.setProduct(prodOpt.get());
        return HttpResponse.ok(orderItemRepository.save(orderItem));
    }

    @Get
    public List<OrderItem> list(@PathVariable Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    @Get("/{itemId}")
    public HttpResponse<OrderItem> get(@PathVariable Long orderId, @PathVariable Long itemId) {
        Optional<OrderItem> itemOpt = orderItemRepository.findById(itemId);
        return itemOpt.filter(item -> item.getOrder().getId().equals(orderId))
                .map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }

    @Put("/{itemId}")
    public HttpResponse<OrderItem> update(@PathVariable Long orderId, @PathVariable Long itemId, @Body OrderItem orderItem) {
        Optional<OrderItem> itemOpt = orderItemRepository.findById(itemId);
        if (itemOpt.isEmpty() || !itemOpt.get().getOrder().getId().equals(orderId))
            return HttpResponse.notFound();
        OrderItem existing = itemOpt.get();
        if (orderItem.getProduct() != null && orderItem.getProduct().getId() != null) {
            Optional<Product> prodOpt = productRepository.findById(orderItem.getProduct().getId());
            prodOpt.ifPresent(existing::setProduct);
        }
        existing.setQuantity(orderItem.getQuantity());
        return HttpResponse.ok(orderItemRepository.save(existing));
    }

    @Delete("/{itemId}")
    public HttpResponse<Void> delete(@PathVariable Long orderId, @PathVariable Long itemId) {
        Optional<OrderItem> itemOpt = orderItemRepository.findById(itemId);
        if (itemOpt.isEmpty() || !itemOpt.get().getOrder().getId().equals(orderId))
            return HttpResponse.notFound();
        orderItemRepository.delete(itemOpt.get());
        return HttpResponse.noContent();
    }
}