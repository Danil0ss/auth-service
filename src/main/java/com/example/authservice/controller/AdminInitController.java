package com.example.authservice.controller;

import com.example.authservice.dto.RegisterRequestDTO;
import com.example.authservice.dto.TokenResponseDTO;
import com.example.authservice.entity.Role;
import com.example.authservice.repository.UserCredentialsRepository;
import com.example.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/admin")
@RequiredArgsConstructor
public class AdminInitController {

    private final AuthService authService;
    private final UserCredentialsRepository repo;

    @PostMapping("/create-first-admin")
    public TokenResponseDTO createFirstAdmin(@RequestBody RegisterRequestDTO dto) {
        if (repo.existsByRole(Role.ADMIN)) {
            throw new RuntimeException("Admin already exists");
        }
        return authService.register(dto);
    }
}