package com.example.shopservice.repository;

import com.example.shopservice.entity.OrderItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class OrderItemRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public OrderItem save(OrderItem orderItem) {
        if (orderItem.getId() == null) {
            entityManager.persist(orderItem);
            return orderItem;
        } else {
            return entityManager.merge(orderItem);
        }
    }

    public Optional<OrderItem> findById(Long id) {
        OrderItem orderItem = entityManager.find(OrderItem.class, id);
        return Optional.ofNullable(orderItem);
    }

    public List<OrderItem> findAll(int page, int size) {
        TypedQuery<OrderItem> query = entityManager.createQuery("SELECT oi FROM OrderItem oi", OrderItem.class);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    public long count() {
        TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(oi) FROM OrderItem oi", Long.class);
        return query.getSingleResult();
    }

    public void delete(OrderItem orderItem) {
        if (entityManager.contains(orderItem)) {
            entityManager.remove(orderItem);
        } else {
            entityManager.remove(entityManager.merge(orderItem));
        }
    }

    public void deleteById(Long id) {
        findById(id).ifPresent(this::delete);
    }
}