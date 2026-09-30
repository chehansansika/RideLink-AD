package com.ridelink.account.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        jwtService.setSecretKey("404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        jwtService.setJwtExpiration(3600000); // 1 hour
    }

    @Test
    @DisplayName("Should generate valid JWT token with subject and claims")
    void testGenerateToken_Success() {
        String token = jwtService.generateToken("user@example.com", "PASSENGER", "acc-123");

        assertNotNull(token);
        assertFalse(token.isEmpty());

        String username = jwtService.extractUsername(token);
        assertEquals("user@example.com", username);

        String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
        assertEquals("PASSENGER", role);

        String accountId = jwtService.extractClaim(token, claims -> claims.get("accountId", String.class));
        assertEquals("acc-123", accountId);
    }

    @Test
    @DisplayName("Should validate token against matching UserDetails")
    void testIsTokenValid_WithUserDetails() {
        String token = jwtService.generateToken("user@example.com", "DRIVER", "acc-456");

        UserDetails userDetails = new User(
                "user@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_DRIVER"))
        );

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    @DisplayName("Should fail validation when username does not match UserDetails")
    void testIsTokenValid_WrongUserDetails() {
        String token = jwtService.generateToken("user@example.com", "DRIVER", "acc-456");

        UserDetails otherUser = new User(
                "other@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_DRIVER"))
        );

        assertFalse(jwtService.isTokenValid(token, otherUser));
    }

    @Test
    @DisplayName("Should validate token string directly")
    void testIsTokenValid_StringOnly() {
        String token = jwtService.generateToken("test@example.com", "ADMIN", "acc-789");

        assertTrue(jwtService.isTokenValid(token));
        assertFalse(jwtService.isTokenValid("invalid.malformed.token"));
    }
}
