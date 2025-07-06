package com.example.shopservice.repository;
import com.example.shopservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User, Long> {}
