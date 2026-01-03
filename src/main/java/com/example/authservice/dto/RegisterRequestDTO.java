package com.example.authservice.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Email must be valid")
        @Size(max = 100)
        String email,

        @NotBlank(message = "Password required")
        @Size(min = 8, max = 200)
        String password,

        @NotBlank(message = "Name is required")
        @Size(max = 100)
        String name,

        @NotBlank(message = "Surname is required")
        @Size(max = 100)
        String surname,

        @NotBlank(message = "Birth date is required")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Format: YYYY-MM-DD")
        String birthDate
) {}