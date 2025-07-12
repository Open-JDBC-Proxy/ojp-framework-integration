package com.example.shopservice.repository;

import com.example.shopservice.entity.Product;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class ProductRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Product save(Product product) {
        if (product.getId() == null) {
            entityManager.persist(product);
            return product;
        } else {
            return entityManager.merge(product);
        }
    }

    public Optional<Product> findById(Long id) {
        Product product = entityManager.find(Product.class, id);
        return Optional.ofNullable(product);
    }

    public List<Product> findAll(int page, int size) {
        TypedQuery<Product> query = entityManager.createQuery("SELECT p FROM Product p", Product.class);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    public long count() {
        TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(p) FROM Product p", Long.class);
        return query.getSingleResult();
    }

    public void delete(Product product) {
        if (entityManager.contains(product)) {
            entityManager.remove(product);
        } else {
            entityManager.remove(entityManager.merge(product));
        }
    }

    public void deleteById(Long id) {
        findById(id).ifPresent(this::delete);
    }
}