package com.example.InnerCityBackend.repository;

import com.example.InnerCityBackend.model.entity.Outreach;
import com.example.InnerCityBackend.model.enums.OutreachStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutreachRepository extends JpaRepository<Outreach, String> {
    List<Outreach> findByStatus(OutreachStatus status);
    List<Outreach> findByCountry(String country);
}
