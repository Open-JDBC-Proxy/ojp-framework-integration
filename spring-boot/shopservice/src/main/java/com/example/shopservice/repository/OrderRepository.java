package com.example.shopservice.repository;
import com.example.shopservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderRepository extends JpaRepository<Order, Long> {}
