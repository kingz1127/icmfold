package com.example.InnerCityBackend.repository;

import com.example.InnerCityBackend.model.entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, String> {
    List<Partner> findAllByOrderByCreatedAtDesc();
}