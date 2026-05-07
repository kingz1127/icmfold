package com.example.InnerCityBackend.repository;

import com.example.InnerCityBackend.model.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByEmailAndCode(String email, String code);
    void deleteByEmail(String email);
}