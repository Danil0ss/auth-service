package com.example.authservice.repository;

import com.example.authservice.entity.Role;
import com.example.authservice.entity.UserCredentials;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Long> {
    Optional<UserCredentials> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByRole(Role role);

    Optional<UserCredentials> findByUserId(Long userId);

}