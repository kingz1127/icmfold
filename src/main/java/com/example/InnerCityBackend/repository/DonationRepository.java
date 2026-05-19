package com.example.InnerCityBackend.repository;

import com.example.InnerCityBackend.model.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, String>, JpaSpecificationExecutor<Donation> {
    Optional<Donation> findByReference(String reference);
    List<Donation> findByEmailOrderByCreatedAtDesc(String email);
}