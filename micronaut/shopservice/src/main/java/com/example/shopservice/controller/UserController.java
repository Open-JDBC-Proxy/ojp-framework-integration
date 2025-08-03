package com.example.shopservice.controller;

import com.example.shopservice.entity.User;
import com.example.shopservice.repository.UserRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@Controller("/users")
public class UserController {
    @Inject
    private UserRepository userRepository;

    @Post
    @Transactional
    public User create(@Body User user) {
        return userRepository.save(user);
    }

    @Get
    public Page<User> list(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Get("/{id}")
    public HttpResponse<User> get(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }

    @Put("/{id}")
    @Transactional
    public HttpResponse<User> update(@PathVariable Long id, @Body User user) {
        return userRepository.findById(id)
                .map(existing -> {
                    // Clear any potential ID from the incoming object to avoid detached entity issues
                    user.setId(null);
                    existing.setUsername(user.getUsername());
                    existing.setEmail(user.getEmail());
                    return HttpResponse.ok(userRepository.save(existing));
                })
                .orElse(HttpResponse.notFound());
    }

    @Delete("/{id}")
    @Transactional
    public HttpResponse<Object> delete(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(existing -> {
                    userRepository.delete(existing);
                    return HttpResponse.noContent();
                })
                .orElse(HttpResponse.notFound());
    }
}