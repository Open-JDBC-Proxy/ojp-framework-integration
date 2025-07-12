package com.example.shopservice.controller;

import com.example.shopservice.entity.Product;
import com.example.shopservice.repository.ProductRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;

@Controller("/products")
public class ProductController {
    @Inject
    private ProductRepository productRepository;

    @Post
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
    public HttpResponse<Product> update(@PathVariable Long id, @Body Product product) {
        return productRepository.findById(id)
                .map(existing -> {
                    existing.setName(product.getName());
                    existing.setPrice(product.getPrice());
                    return HttpResponse.ok(productRepository.save(existing));
                })
                .orElse(HttpResponse.notFound());
    }

    @Delete("/{id}")
    public HttpResponse<Object> delete(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(existing -> {
                    productRepository.delete(existing);
                    return HttpResponse.noContent();
                })
                .orElse(HttpResponse.notFound());
    }
}