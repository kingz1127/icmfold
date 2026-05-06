package com.example.InnerCityBackend.repository;

import com.example.InnerCityBackend.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    // You DON'T need to write save() or findAll() here anymore.
    // JpaRepository provides them automatically!

    // This is a custom query. Spring will implement the logic for this automatically.
    boolean existsByName(String name);
}