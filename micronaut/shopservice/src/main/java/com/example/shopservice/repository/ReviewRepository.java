package com.example.shopservice.repository;

import com.example.shopservice.entity.Review;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByUserId(Long userId, Pageable pageable);
    Page<Review> findByProductId(Long productId, Pageable pageable);
}