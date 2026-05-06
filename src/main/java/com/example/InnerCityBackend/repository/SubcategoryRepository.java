package com.example.InnerCityBackend.repository;

import com.example.InnerCityBackend.model.entity.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, String> {

    // Spring Data JPA already provides:
    // Optional<Subcategory> findById(String id);

    // Add this helper to check for duplicates by name within a category
    boolean existsByNameAndCategoryId(String name, String categoryId);
}