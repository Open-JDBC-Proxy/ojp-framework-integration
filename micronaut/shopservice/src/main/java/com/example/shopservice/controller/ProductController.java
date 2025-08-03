package com.example.shopservice.controller;

import com.example.shopservice.entity.Product;
import com.example.shopservice.repository.ProductRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@Controller("/products")
public class ProductController {
    @Inject
    private ProductRepository productRepository;

    @Post
    @Transactional
    public Product create(@Body Product product) {
        return productRepository.save(product);
    }

    @Get
    public Page<Product> list(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Get("/{id}")
    public HttpResponse<Product> get(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }

    @Put("/{id}")
    @Transactional
    public HttpResponse<Product> update(@PathVariable Long id, @Body Product product) {
        return productRepository.findById(id)
                .map(existing -> {
                    // Clear any potential ID from the incoming object to avoid detached entity issues
                    product.setId(null);
                    existing.setName(product.getName());
                    existing.setPrice(product.getPrice());
                    return HttpResponse.ok(productRepository.save(existing));
                })
                .orElse(HttpResponse.notFound());
    }

    @Delete("/{id}")
    @Transactional
    public HttpResponse<Object> delete(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(existing -> {
                    productRepository.delete(existing);
                    return HttpResponse.noContent();
                })
                .orElse(HttpResponse.notFound());
    }
}