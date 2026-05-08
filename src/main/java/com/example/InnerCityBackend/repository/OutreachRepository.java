package com.example.InnerCityBackend.repository;

import com.example.InnerCityBackend.model.entity.Outreach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OutreachRepository extends JpaRepository<Outreach, String> {
    // You can add custom search methods here if needed, e.g.
    // List<Outreach> findByCountry(String country);
}