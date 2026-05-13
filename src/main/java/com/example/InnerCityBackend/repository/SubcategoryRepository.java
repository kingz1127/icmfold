

package com.example.InnerCityBackend.repository;

import com.example.InnerCityBackend.model.entity.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, String> {

    // Basic queries
    List<Subcategory> findByCategoryId(String categoryId);

    boolean existsByCategoryId(String categoryId);

    long countByCategoryId(String categoryId);

    // Check for duplicate names within a category
    boolean existsByCategoryIdAndName(String categoryId, String name);

    // Check for duplicate names excluding a specific subcategory (for updates)
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM Subcategory s " +
            "WHERE s.category.id = :categoryId " +
            "AND s.name = :name " +
            "AND s.id != :id")
    boolean existsByCategoryIdAndNameAndIdNot(@Param("categoryId") String categoryId,
                                              @Param("name") String name,
                                              @Param("id") String id);

    // Check if subcategory has outreaches (original query)
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM Subcategory s WHERE s.category.id = :categoryId " +
            "AND s.id IN (SELECT o.subcategory.id FROM Outreach o WHERE o.subcategory.id IS NOT NULL)")
    boolean existsSubcategoryWithOutreachesByCategoryId(@Param("categoryId") String categoryId);

    // Check if a specific subcategory has outreaches
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END " +
            "FROM Outreach o WHERE o.subcategory.id = :subcategoryId")
    boolean existsOutreachBySubcategoryId(@Param("subcategoryId") String subcategoryId);

    // Count outreaches for a specific subcategory
    @Query("SELECT COUNT(o) FROM Outreach o WHERE o.subcategory.id = :subcategoryId")
    long countOutreachesBySubcategoryId(@Param("subcategoryId") String subcategoryId);

    // Find subcategories with their categories loaded eagerly
    @Query("SELECT DISTINCT s FROM Subcategory s LEFT JOIN FETCH s.category WHERE s.category.id = :categoryId")
    List<Subcategory> findByCategoryIdWithCategory(@Param("categoryId") String categoryId);

    // Find subcategory by ID with category loaded eagerly
    @Query("SELECT s FROM Subcategory s LEFT JOIN FETCH s.category WHERE s.id = :id")
    Optional<Subcategory> findByIdWithCategory(@Param("id") String id);

    // Find all subcategories with their categories loaded eagerly
    @Query("SELECT DISTINCT s FROM Subcategory s LEFT JOIN FETCH s.category")
    List<Subcategory> findAllWithCategory();

    // Find subcategories by category ID ordered by name
    List<Subcategory> findByCategoryIdOrderByNameAsc(String categoryId);

    // Search subcategories by name (case insensitive)
    List<Subcategory> findByNameContainingIgnoreCase(String name);

    // Search subcategories within a category by name (case insensitive)
    List<Subcategory> findByCategoryIdAndNameContainingIgnoreCase(String categoryId, String name);

    // Count subcategories by category ID that have outreaches
    @Query("SELECT COUNT(DISTINCT s) FROM Subcategory s " +
            "WHERE s.category.id = :categoryId " +
            "AND EXISTS (SELECT o FROM Outreach o WHERE o.subcategory.id = s.id)")
    long countSubcategoriesWithOutreachesByCategoryId(@Param("categoryId") String categoryId);

    // Get all subcategory IDs for a category
    @Query("SELECT s.id FROM Subcategory s WHERE s.category.id = :categoryId")
    List<String> findIdsByCategoryId(@Param("categoryId") String categoryId);

    // Delete all subcategories for a category
    @Query("DELETE FROM Subcategory s WHERE s.category.id = :categoryId")
    void deleteByCategoryId(@Param("categoryId") String categoryId);

    // Check if subcategory exists by name (case insensitive)
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM Subcategory s WHERE LOWER(s.name) = LOWER(:name)")
    boolean existsByNameIgnoreCase(@Param("name") String name);

    // Get subcategories with outreach count
    @Query("SELECT s, COUNT(o) as outreachCount " +
            "FROM Subcategory s " +
            "LEFT JOIN Outreach o ON o.subcategory.id = s.id " +
            "WHERE s.category.id = :categoryId " +
            "GROUP BY s.id")
    List<Object[]> findSubcategoriesWithOutreachCount(@Param("categoryId") String categoryId);
}