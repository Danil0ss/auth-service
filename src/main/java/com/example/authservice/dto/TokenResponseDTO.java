package com.example.authservice.dto;

public record TokenResponseDTO(
        String accessToken,
        String refreshToken
) {}