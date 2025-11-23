package com.example.authservice.dto;

import com.example.authservice.entity.Role;
import java.time.OffsetDateTime;

public record UserResponseDTO(
        Long id,
        String email,
        Role role,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}