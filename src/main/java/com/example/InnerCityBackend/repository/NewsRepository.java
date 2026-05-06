package com.example.InnerCityBackend.repository;

import com.example.InnerCityBackend.model.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, String> {

    // Returns news sorted by newest date first
    List<News> findAllByOrderByCreatedAtDesc();

    // Optional: Filter news by country
    List<News> findByCountry(String country);

    // Optional: Get all global news
    List<News> findByIsGlobalTrue();
}