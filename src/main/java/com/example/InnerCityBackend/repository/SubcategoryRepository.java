package com.example.InnerCityBackend.repository;

import com.example.InnerCityBackend.model.entity.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, String> {
    List<Subcategory> findByCategoryId(String categoryId);

    boolean existsByCategoryId(String categoryId);

    long countByCategoryId(String categoryId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM Subcategory s WHERE s.category.id = :categoryId " +
            "AND s.id IN (SELECT o.subcategory.id FROM Outreach o WHERE o.subcategory.id IS NOT NULL)")
    boolean existsSubcategoryWithOutreachesByCategoryId(@Param("categoryId") String categoryId);
}