package com.example.demo.auth;

import com.example.demo.auth.dto.LoginRequest;
import com.example.demo.auth.dto.LoginResponse;
import com.example.demo.auth.dto.RegisterRequest;
import com.example.demo.auth.dto.RegisterResponse;
import com.example.demo.common.exception.ApiException;
import com.example.demo.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private AuthService authService;

    @Test
    void doLogin_success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Test123!");
        User user = new User();
        user.setUsername("testuser");
        user.setPasswordHash("hashed");
        user.setEnabled(true);
        user.setRole(Role.AGENCY);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Test123!", "hashed")).thenReturn(true);
        when(jwtService.generateToken("testuser", "AGENCY")).thenReturn("jwt-token");
        LoginResponse response = authService.doLogin(request);
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getRole()).isEqualTo("AGENCY");
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("Test123!", "hashed");
        verify(jwtService).generateToken("testuser", "AGENCY");
    }

    @Test
    void doLogin_userNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("notfound");
        request.setPassword("Test123!");
        when(userRepository.findByUsername("notfound")).thenReturn(Optional.empty());
        Throwable thrown = catchThrowable(() -> authService.doLogin(request));
        assertThat(thrown).isInstanceOf(ApiException.class)
            .hasMessageContaining("auth.invalid.credentials");
        verify(userRepository).findByUsername("notfound");
    }

    @Test
    void register_success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("Test123!");
        request.setRole(Role.AGENCY);
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Test123!")).thenReturn("hashed");
        User user = new User();
        user.setUsername("newuser");
        user.setPasswordHash("hashed");
        user.setRole(Role.AGENCY);
        user.setEnabled(true);
        when(userRepository.save(any(User.class))).thenReturn(user);
        RegisterResponse response = authService.register(request);
        assertThat(response.getUsername()).isEqualTo("newuser");
        assertThat(response.getRole()).isEqualTo("AGENCY");
        verify(userRepository).findByUsername("newuser");
        verify(passwordEncoder).encode("Test123!");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_usernameExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existing");
        request.setPassword("Test123!");
        request.setRole(Role.AGENCY);
        User user = new User();
        user.setUsername("existing");
        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(user));
        Throwable thrown = catchThrowable(() -> authService.register(request));
        assertThat(thrown).isInstanceOf(ApiException.class)
            .hasMessageContaining("validation.username.exists");
        verify(userRepository).findByUsername("existing");
    }

    @Test
    void register_invalidUsername() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("ab");
        request.setPassword("Test123!");
        request.setRole(Role.AGENCY);
        Throwable thrown = catchThrowable(() -> authService.register(request));
        assertThat(thrown).isInstanceOf(ApiException.class)
            .hasMessageContaining("validation.username.minLength");
    }
}