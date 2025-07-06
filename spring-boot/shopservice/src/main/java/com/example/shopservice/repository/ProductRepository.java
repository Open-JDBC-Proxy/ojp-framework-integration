package com.example.shopservice.repository;
import com.example.shopservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductRepository extends JpaRepository<Product, Long> {}
