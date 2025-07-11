package com.example.shopservice.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.math.BigDecimal;

@Entity
public class Product extends PanacheEntity {
    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public BigDecimal price;
}
