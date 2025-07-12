package com.example.shopservice.controller;

import com.example.shopservice.entity.Product;
import com.example.shopservice.entity.Review;
import com.example.shopservice.entity.User;
import com.example.shopservice.repository.ProductRepository;
import com.example.shopservice.repository.ReviewRepository;
import com.example.shopservice.repository.UserRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;

import java.util.Optional;

@Controller("/reviews")
public class ReviewController {
    @Inject
    private ReviewRepository reviewRepository;
    @Inject
    private UserRepository userRepository;
    @Inject
    private ProductRepository productRepository;

    @Post
    public HttpResponse<Review> create(@Body Review review) {
        if (review.getUser() == null || review.getUser().getId() == null)
            return HttpResponse.badRequest();
        if (review.getProduct() == null || review.getProduct().getId() == null)
            return HttpResponse.badRequest();

        Optional<User> userOpt = userRepository.findById(review.getUser().getId());
        Optional<Product> prodOpt = productRepository.findById(review.getProduct().getId());

        if (userOpt.isEmpty() || prodOpt.isEmpty())
            return HttpResponse.badRequest();

        review.setUser(userOpt.get());
        review.setProduct(prodOpt.get());
        return HttpResponse.ok(reviewRepository.save(review));
    }

    @Get
    public Page<Review> list(Pageable pageable, @QueryValue Optional<Long> userId, @QueryValue Optional<Long> productId) {
        if (userId.isPresent()) {
            return reviewRepository.findByUserId(userId.get(), pageable);
        } else if (productId.isPresent()) {
            return reviewRepository.findByProductId(productId.get(), pageable);
        } else {
            return reviewRepository.findAll(pageable);
        }
    }

    @Get("/{id}")
    public HttpResponse<Review> get(@PathVariable Long id) {
        return reviewRepository.findById(id)
                .map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }

    @Put("/{id}")
    public HttpResponse<Review> update(@PathVariable Long id, @Body Review review) {
        return reviewRepository.findById(id)
                .map(existing -> {
                    if (review.getUser() != null && review.getUser().getId() != null) {
                        userRepository.findById(review.getUser().getId()).ifPresent(existing::setUser);
                    }
                    if (review.getProduct() != null && review.getProduct().getId() != null) {
                        productRepository.findById(review.getProduct().getId()).ifPresent(existing::setProduct);
                    }
                    existing.setRating(review.getRating());
                    existing.setComment(review.getComment());
                    return HttpResponse.ok(reviewRepository.save(existing));
                })
                .orElse(HttpResponse.notFound());
    }

    @Delete("/{id}")
    public HttpResponse<Object> delete(@PathVariable Long id) {
        return reviewRepository.findById(id)
                .map(existing -> {
                    reviewRepository.delete(existing);
                    return HttpResponse.noContent();
                })
                .orElse(HttpResponse.notFound());
    }
}