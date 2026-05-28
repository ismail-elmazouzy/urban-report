package com.urbanreport.service;

import com.urbanreport.dto.*;
import com.urbanreport.entity.User;
import com.urbanreport.repository.UserRepository;
import com.urbanreport.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository    userRepository;
    private final PasswordEncoder   passwordEncoder;
    private final JwtUtil           jwtUtil;

    private static final String SUPER_ADMIN_EMAIL = "admin@urbanreport.ma";

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }


        User.Role role = req.getEmail().equals(SUPER_ADMIN_EMAIL)
                ? User.Role.SUPER_ADMIN
                : User.Role.USER;

        User user = User.builder()
                .nom(req.getNom())
                .prenom(req.getPrenom())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(role)
                .build();

        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getRole().name(), user.getEmail(), user.getPhotoUrl(), user.getZone());
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Email introuvable"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getRole().name(), user.getEmail(), user.getPhotoUrl(), user.getZone());
    }
}