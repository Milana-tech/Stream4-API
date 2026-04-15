package com.stream.four.service.auth;

import com.stream.four.model.enums.Role;
import com.stream.four.model.user.User;
import com.stream.four.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserDetailsServiceImpl service = new UserDetailsServiceImpl(userRepository);

    @Test
    void loadUserByUsername_existingUser_returnsUserDetailsWithCorrectAuthority() {
        var user = new User();
        user.setUserId("u1");
        user.setPassword("hashed");
        user.setRole(Role.USER);

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        var result = service.loadUserByUsername("u1");

        assertEquals("u1", result.getUsername());
        assertEquals("hashed", result.getPassword());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_employeeRole_returnsCorrectAuthority() {
        var user = new User();
        user.setUserId("emp-1");
        user.setPassword("hashed");
        user.setRole(Role.SENIOR_EMPLOYEE);

        when(userRepository.findById("emp-1")).thenReturn(Optional.of(user));

        var result = service.loadUserByUsername("emp-1");

        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SENIOR_EMPLOYEE")));
    }

    @Test
    void loadUserByUsername_userNotFound_throwsUsernameNotFoundException() {
        when(userRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing"));
    }
}
