package com.stream.four.service;

import com.stream.four.dto.response.user.LoginRequest;
import com.stream.four.model.user.User;
import com.stream.four.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final LoginService loginService = new LoginService(userRepository, passwordEncoder);

    @Test
    void login_withEmail_andCorrectPassword_returnsUser() {
        var req = new LoginRequest();
        req.setLogin("a@b.com");
        req.setPassword("pw");

        var user = new User();
        user.setEmail("a@b.com");
        user.setPassword("encoded");
        user.setVerified(true);

        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pw", "encoded")).thenReturn(true);

        var result = loginService.login(req);

        assertSame(user, result);
        assertEquals(0, user.getFailedLoginAttempts());
    }

    @Test
    void login_withUsername_andCorrectPassword_returnsUser() {
        var req = new LoginRequest();
        req.setLogin("milan");
        req.setPassword("pw");

        var user = new User();
        user.setName("milan");
        user.setPassword("encoded");
        user.setVerified(true);

        when(userRepository.findByName("milan")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pw", "encoded")).thenReturn(true);

        var result = loginService.login(req);

        assertSame(user, result);
        verify(userRepository).findByName("milan");
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void login_unverifiedAccount_throws() {
        var req = new LoginRequest();
        req.setLogin("a@b.com");
        req.setPassword("pw");

        var user = new User();
        user.setEmail("a@b.com");
        user.setPassword("encoded");
        user.setVerified(false);

        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));

        assertThrows(IllegalStateException.class, () -> loginService.login(req));
    }

    @Test
    void login_wrongPassword_incrementsAttempts_andThrows() {
        var req = new LoginRequest();
        req.setLogin("a@b.com");
        req.setPassword("wrong");

        var user = new User();
        user.setEmail("a@b.com");
        user.setPassword("encoded");
        user.setVerified(true);
        user.setFailedLoginAttempts(0);

        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> loginService.login(req));
        assertEquals(1, user.getFailedLoginAttempts());
    }

    @Test
    void login_thirdFailedAttempt_blocksAccount() {
        var req = new LoginRequest();
        req.setLogin("a@b.com");
        req.setPassword("wrong");

        var user = new User();
        user.setEmail("a@b.com");
        user.setPassword("encoded");
        user.setVerified(true);
        user.setFailedLoginAttempts(2);

        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        var ex = assertThrows(IllegalStateException.class, () -> loginService.login(req));
        assertTrue(ex.getMessage().toLowerCase().contains("blocked"));
        assertEquals(3, user.getFailedLoginAttempts());
    }

    @Test
    void login_alreadyBlocked_throws() {
        var req = new LoginRequest();
        req.setLogin("a@b.com");
        req.setPassword("pw");

        var user = new User();
        user.setEmail("a@b.com");
        user.setPassword("encoded");
        user.setVerified(true);
        user.setFailedLoginAttempts(3);

        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));

        assertThrows(IllegalStateException.class, () -> loginService.login(req));
    }

    @Test
    void login_expiredLock_resetsCounterAndSucceeds() {
        var req = new LoginRequest();
        req.setLogin("a@b.com");
        req.setPassword("pw");

        var user = new User();
        user.setEmail("a@b.com");
        user.setPassword("encoded");
        user.setVerified(true);
        user.setFailedLoginAttempts(3);
        user.setLockedUntil(LocalDateTime.now().minusMinutes(1)); // lock expired

        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pw", "encoded")).thenReturn(true);

        var result = loginService.login(req);

        assertSame(user, result);
        assertEquals(0, user.getFailedLoginAttempts());
    }

    @Test
    void login_userNotFound_throws() {
        var req = new LoginRequest();
        req.setLogin("missing@b.com");
        req.setPassword("pw");

        when(userRepository.findByEmail("missing@b.com")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> loginService.login(req));
    }

    @Test
    void isValidEmail_nullOrBlank_false() {
        assertFalse(LoginService.isValidEmail(null));
        assertFalse(LoginService.isValidEmail(""));
        assertFalse(LoginService.isValidEmail("   "));
    }

    @Test
    void isValidEmail_validAndInvalid() {
        assertTrue(LoginService.isValidEmail("a@b.com"));
        assertFalse(LoginService.isValidEmail("not-an-email"));
    }
}
