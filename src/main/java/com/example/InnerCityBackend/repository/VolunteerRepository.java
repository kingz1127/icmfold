package com.example.InnerCityBackend.repository;

import com.example.InnerCityBackend.model.entity.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer, String>, JpaSpecificationExecutor<Volunteer> {
    boolean existsByEmail(String email);
}