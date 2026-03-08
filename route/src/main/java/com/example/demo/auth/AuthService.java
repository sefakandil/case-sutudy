package com.example.demo.auth;

import com.example.demo.auth.dto.LoginRequest;
import com.example.demo.auth.dto.LoginResponse;
import com.example.demo.auth.dto.RegisterRequest;
import com.example.demo.auth.dto.RegisterResponse;
import com.example.demo.common.exception.ApiException;
import com.example.demo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;


    public LoginResponse doLogin(LoginRequest req) {

        final User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new ApiException(
                        HttpStatus.UNAUTHORIZED,
                        "auth.invalid.credentials"
                ));

        if (!user.isEnabled() || !passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new ApiException(
                    HttpStatus.UNAUTHORIZED,
                    "auth.invalid.credentials"
            );
        }

        final String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
        return LoginResponse.builder().token(token).username(user.getUsername()).role(user.getRole().toString()).build();
    }

    public RegisterResponse register(RegisterRequest req) {

        validateUsername(req.getUsername());
        validatePassword(req.getPassword());
        validateUsernameNotExists(req.getUsername());

        final User user = new User();
        user.setUsername(req.getUsername().trim());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setRole(req.getRole());
        user.setEnabled(true);

        final User saved = userRepository.save(user);

        return RegisterResponse.builder()
                .username(saved.getUsername())
                .role(saved.getRole().name())
                .build();
    }

    private void validatePassword(String password) {

        if (password == null || password.trim().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "validation.password.required");
        }

        if (password.length() < 8) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "validation.password.minLength",
                    8);
        }

        if (password.length() > 50) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "validation.password.maxLength",
                    50);
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "validation.password.uppercase");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "validation.password.lowercase");
        }

        if (!password.matches(".*\\d.*")) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "validation.password.digit");
        }

        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "validation.password.special");
        }
    }

    private void validateUsername(String username) {

        if (username == null || username.trim().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "validation.username.required");
        }

        String trimmedUsername = username.trim();

        if (trimmedUsername.length() < 3) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "validation.username.minLength",
                    3);
        }

        if (trimmedUsername.length() > 50) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "validation.username.maxLength",
                    50);
        }

        if (!trimmedUsername.matches("^[a-zA-Z0-9_-]+$")) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "validation.username.pattern");
        }
    }

    private void validateUsernameNotExists(String username) {

        Optional<User> existingUser = userRepository.findByUsername(username.trim());

        if (existingUser.isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "validation.username.exists",
                    username);
        }
    }
}
