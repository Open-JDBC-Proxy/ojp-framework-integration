package com.example.shopservice.repository;
import com.example.shopservice.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByUserId(Long userId, Pageable pageable);
    Page<Review> findByProductId(Long productId, Pageable pageable);
}
