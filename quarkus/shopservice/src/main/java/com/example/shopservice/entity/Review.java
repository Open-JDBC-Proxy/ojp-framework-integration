package com.example.shopservice.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity
public class Review extends PanacheEntity {
    @ManyToOne(optional = false)
    public User user;

    @ManyToOne(optional = false)
    public Product product;

    @Column(nullable = false)
    public int rating;

    @Column(length = 1000)
    public String comment;
}