package com.example.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Email must be valid")
        @Size(max = 100)
        String email,

        @NotBlank(message = "Password required")
        @Size(min = 8, max = 200)
        String password
) {}