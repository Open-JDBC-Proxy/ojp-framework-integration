package com.example.shopservice.repository;

import com.example.shopservice.entity.Review;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class ReviewRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Review save(Review review) {
        if (review.getId() == null) {
            entityManager.persist(review);
            return review;
        } else {
            return entityManager.merge(review);
        }
    }

    public Optional<Review> findById(Long id) {
        Review review = entityManager.find(Review.class, id);
        return Optional.ofNullable(review);
    }

    public List<Review> findAll(int page, int size) {
        TypedQuery<Review> query = entityManager.createQuery("SELECT r FROM Review r", Review.class);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    public long count() {
        TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(r) FROM Review r", Long.class);
        return query.getSingleResult();
    }

    public void delete(Review review) {
        if (entityManager.contains(review)) {
            entityManager.remove(review);
        } else {
            entityManager.remove(entityManager.merge(review));
        }
    }

    public void deleteById(Long id) {
        findById(id).ifPresent(this::delete);
    }
}