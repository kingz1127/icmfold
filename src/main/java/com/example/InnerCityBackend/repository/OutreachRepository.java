package com.example.InnerCityBackend.repository;

import com.example.InnerCityBackend.model.entity.Outreach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutreachRepository extends JpaRepository<Outreach, String> {
    boolean existsBySubcategoryId(String subcategoryId);

    long countBySubcategoryId(String subcategoryId);

    List<Outreach> findBySubcategoryId(String subcategoryId);
}