package com.stream.four.service.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    @Test
    void generateToken_thenParseUserIdAndRole_andValidate() {
        // 32+ chars required for HS256.
        var jwtService = new JwtService("01234567890123456789012345678901", 60_000);

        var token = jwtService.generateToken("user-1", "ADMIN");

        assertNotNull(token);
        assertTrue(jwtService.validateToken(token));
        assertEquals("user-1", jwtService.getUserId(token));
        assertEquals("ADMIN", jwtService.getRole(token));
    }

    @Test
    void validateToken_invalid_returnsFalse() {
        var jwtService = new JwtService("01234567890123456789012345678901", 60_000);
        assertFalse(jwtService.validateToken("not-a-jwt"));
    }
}

