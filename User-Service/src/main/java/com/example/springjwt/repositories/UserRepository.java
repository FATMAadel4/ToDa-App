package com.example.springjwt.repositories;

import com.example.springjwt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
}


