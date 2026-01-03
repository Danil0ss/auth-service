package com.example.authservice.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Email must be valid")
        @Size(max = 100, message = "Email must be less than 100 characters")
        String email,

        @NotBlank(message = "Password required")
        @Size(min = 8, max = 200, message = "Password must be at least 8 symbols")
        String password
) {}