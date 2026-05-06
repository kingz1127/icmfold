package com.example.InnerCityBackend.repository;

import com.example.InnerCityBackend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByEmail(String email);

    // Use Optional instead of ScopedValue
    Optional<User> findByEmail(String email);

    Optional<User> findByKingschatId(String kingschatId);
}