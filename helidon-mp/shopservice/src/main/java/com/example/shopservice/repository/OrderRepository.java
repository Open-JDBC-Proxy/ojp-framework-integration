package com.example.shopservice.repository;

import com.example.shopservice.entity.Order;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class OrderRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Order save(Order order) {
        if (order.getId() == null) {
            entityManager.persist(order);
            return order;
        } else {
            return entityManager.merge(order);
        }
    }

    public Optional<Order> findById(Long id) {
        Order order = entityManager.find(Order.class, id);
        return Optional.ofNullable(order);
    }

    public List<Order> findAll(int page, int size) {
        TypedQuery<Order> query = entityManager.createQuery("SELECT o FROM Order o", Order.class);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    public long count() {
        TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(o) FROM Order o", Long.class);
        return query.getSingleResult();
    }

    public void delete(Order order) {
        if (entityManager.contains(order)) {
            entityManager.remove(order);
        } else {
            entityManager.remove(entityManager.merge(order));
        }
    }

    public void deleteById(Long id) {
        findById(id).ifPresent(this::delete);
    }
}