package com.stream.four.service;

import com.stream.four.dto.response.user.LoginRequest;
import com.stream.four.model.user.User;
import com.stream.four.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final LoginService loginService = new LoginService(userRepository);

    @Test
    void login_withEmail_andCorrectPassword_returnsUser() {
        var req = new LoginRequest();
        req.setLogin("a@b.com");
        req.setPassword("pw");

        var user = new User();
        user.setEmail("a@b.com");
        user.setPassword("pw");

        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));

        var result = loginService.login(req);

        assertSame(user, result);
        verify(userRepository).findByEmail("a@b.com");
        verify(userRepository, never()).findByName(anyString());
    }

    @Test
    void login_withUsername_andCorrectPassword_returnsUser() {
        var req = new LoginRequest();
        req.setLogin("milan");
        req.setPassword("pw");

        var user = new User();
        user.setName("milan");
        user.setPassword("pw");

        when(userRepository.findByName("milan")).thenReturn(Optional.of(user));

        var result = loginService.login(req);

        assertSame(user, result);
        verify(userRepository).findByName("milan");
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void login_wrongPassword_throws() {
        var req = new LoginRequest();
        req.setLogin("a@b.com");
        req.setPassword("wrong");

        var user = new User();
        user.setEmail("a@b.com");
        user.setPassword("pw");

        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));

        var ex = assertThrows(IllegalArgumentException.class, () -> loginService.login(req));
        assertTrue(ex.getMessage().toLowerCase().contains("incorrect"));
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

