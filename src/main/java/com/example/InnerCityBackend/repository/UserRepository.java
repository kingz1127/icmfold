package com.example.InnerCityBackend.repository;

import com.example.InnerCityBackend.model.entity.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

    boolean existsByEmail(String email);

    // Use Optional instead of ScopedValue
    Optional<User> findByEmail(String email);

    Optional<User> findByKingschatId(String kingschatId);


    Optional<User> findByResetToken(String token);

}