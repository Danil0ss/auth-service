package com.example.authservice.service;

import com.example.authservice.dto.LoginRequestDTO;
import com.example.authservice.dto.RegisterRequestDTO;
import com.example.authservice.dto.TokenResponseDTO;
import com.example.authservice.entity.Role;
import com.example.authservice.entity.UserCredentials;
import com.example.authservice.repository.UserCredentialsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserCredentialsRepository userCredentialsRepository;
    private final JwtService jwtService;


    @Transactional
    public TokenResponseDTO register(RegisterRequestDTO dto) {
        if (userCredentialsRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("User already exists");
        }

        UserCredentials credentials = new UserCredentials();
        credentials.setEmail(dto.email());
        credentials.setHashedPassword(passwordEncoder.encode(dto.password()));
        credentials.setRole(Role.USER);

        credentials.setUserId(-System.nanoTime());

        userCredentialsRepository.save(credentials);

        Long authId = credentials.getId();

        String accessToken = jwtService.generateAccessToken(authId, Role.USER);
        String refreshToken = jwtService.generateRefreshToken(authId);

        return new TokenResponseDTO(accessToken, refreshToken);
    }

    public TokenResponseDTO login(LoginRequestDTO requestDTO){
        String email=requestDTO.email();
        String password= requestDTO.password();

       UserCredentials user= userCredentialsRepository.findByEmail(email).orElseThrow(()->
               new RuntimeException("User with "+ email +" not found"));

       if(passwordEncoder.matches(password, user.getHashedPassword())){

           return new TokenResponseDTO(
                   jwtService.generateAccessToken(user.getUserId(), user.getRole()),
                   jwtService.generateRefreshToken(user.getUserId())
           );
       }
       else throw new RuntimeException("Wrong password");
    }

    @Transactional
    public TokenResponseDTO refreshToken(String refreshToken) {
        try {
            Long userId = jwtService.extractUserId(refreshToken);

            UserCredentials user = userCredentialsRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String newAccessToken = jwtService.generateAccessToken(userId, user.getRole());
            String newRefreshToken = jwtService.generateRefreshToken(userId);

            return new TokenResponseDTO(newAccessToken, newRefreshToken);
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token: " + e.getMessage());
        }
    }


    @Transactional
    public void deleteUserByEmail(String email) {
        UserCredentials user = userCredentialsRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found for rollback"));
        userCredentialsRepository.delete(user);
    }

    @Transactional
    public void updateUserId(String email, Long userId) {
        UserCredentials user = userCredentialsRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setUserId(userId);
        userCredentialsRepository.save(user);
    }
}
