package com.example.shopservice.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity
public class OrderItem extends PanacheEntity {
    @ManyToOne(optional = false)
    public Order order;

    @ManyToOne(optional = false)
    public Product product;

    @Column(nullable = false)
    public int quantity;
}
