package com.example.demo.security;

import com.example.demo.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    @Mock
    private JwtProperties jwtProperties;
    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setup() {
        lenient().when(jwtProperties.getSecret()).thenReturn("mysecretkeymysecretkeymysecretkeymysecretkey");
        lenient().when(jwtProperties.getExpiration()).thenReturn(3600000L);
    }

    @Test
    void generateToken_shouldContainUsernameAndRole() {
        String username = "testuser";
        String role = "ADMIN";
        String token = jwtService.generateToken(username, role);
        assertThat(token).isNotNull();
        assertThat(token.length()).isGreaterThan(0);
        Jws<Claims> parsed = jwtService.parse(token);
        assertThat(parsed.getPayload().getSubject()).isEqualTo(username);
        assertThat(parsed.getPayload().get("role")).isEqualTo(role);
    }

    @Test
    void parse_shouldThrowExceptionForInvalidToken() {
        String invalidToken = "invalid.token.value";
        Throwable thrown = catchThrowable(() -> jwtService.parse(invalidToken));
        assertThat(thrown).isInstanceOf(JwtException.class);
    }
}