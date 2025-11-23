package com.example.authservice.service;

import com.example.authservice.dto.LoginRequestDTO;
import com.example.authservice.dto.RegisterRequestDTO;
import com.example.authservice.dto.TokenResponseDTO;
import com.example.authservice.entity.Role;
import com.example.authservice.entity.UserCredentials;
import com.example.authservice.repository.UserCredentialsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserCredentialsRepository userCredentialsRepository;
    private final JwtService jwtService;
    private final RestTemplate restTemplate;

    @Transactional
    public TokenResponseDTO register(RegisterRequestDTO dto) {

        if (userCredentialsRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("User with email " + dto.email() + " already exists");
        }

        Long userId = createUserInUserService(dto);

        UserCredentials credentials = new UserCredentials();
        credentials.setEmail(dto.email());
        credentials.setHashedPassword(passwordEncoder.encode(dto.password()));
        credentials.setRole(Role.USER);
        credentials.setUserId(userId);

        userCredentialsRepository.save(credentials);

        String accessToken = jwtService.generateAccessToken(userId, Role.USER);
        String refreshToken = jwtService.generateRefreshToken(userId);

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

            UserCredentials user = userCredentialsRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String newAccessToken = jwtService.generateAccessToken(userId, user.getRole());
            String newRefreshToken = jwtService.generateRefreshToken(userId);

            return new TokenResponseDTO(newAccessToken, newRefreshToken);

        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token: " + e.getMessage());
        }
    }

    private Long createUserInUserService(RegisterRequestDTO dto) {
        String url = "http://localhost:8081/api/users/internal/create";

        Map<String, Object> request = new HashMap<>();
        request.put("name", dto.name());
        request.put("surname", dto.surname());
        request.put("birthDate", dto.birthDate());
        request.put("email", dto.email());

        ResponseEntity<Long> response = restTemplate.postForEntity(url, request, Long.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to create user in UserService");
        }
    }

}
