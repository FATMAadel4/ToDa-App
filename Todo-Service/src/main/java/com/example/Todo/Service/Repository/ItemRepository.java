package com.example.Todo.Service.Repository;

import com.example.Todo.Service.Entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByTitleContaining(String title);
}

