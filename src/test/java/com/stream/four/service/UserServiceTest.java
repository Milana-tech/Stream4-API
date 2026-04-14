package com.stream.four.service;

import com.stream.four.dto.requests.CreateUserRequest;
import com.stream.four.dto.response.user.UserResponse;
import com.stream.four.mapper.UserMapper;
import com.stream.four.model.user.User;
import com.stream.four.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserMapper userMapper = mock(UserMapper.class);
    private final EmailService emailService = mock(EmailService.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final TrialService trialService = mock(TrialService.class);

    private final UserService userService = new UserService(userRepository, userMapper, emailService, passwordEncoder, trialService);

    @Test
    void getAllUsers_mapsEntitiesToDtos() {
        var u1 = new User();
        var u2 = new User();

        when(userRepository.findAll()).thenReturn(List.of(u1, u2));
        when(userMapper.toDto(u1)).thenReturn(new UserResponse());
        when(userMapper.toDto(u2)).thenReturn(new UserResponse());

        var result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void getUser_existing_returnsDto() {
        var user = new User();
        var dto = new UserResponse();

        when(userRepository.findById("u")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        assertSame(dto, userService.getUser("u"));
    }

    @Test
    void getUser_missing_throws() {
        when(userRepository.findById("missing")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.getUser("missing"));
    }

    @Test
    void createUser_encodesPassword_setsVerificationToken_sendsEmail_andReturnsDto() {
        var req = new CreateUserRequest();
        req.setEmail("a@b.com");
        req.setPassword("plaintext");

        var entity = new User();
        var saved = new User();
        saved.setEmail("a@b.com");
        saved.setVerificationToken("token-123");
        var dto = new UserResponse();

        when(userMapper.toEntity(req)).thenReturn(entity);
        when(passwordEncoder.encode("plaintext")).thenReturn("encoded");
        when(userRepository.save(entity)).thenReturn(saved);
        when(userMapper.toDto(saved)).thenReturn(dto);

        var result = userService.createUser(req);

        assertSame(dto, result);
        assertEquals("encoded", entity.getPassword());
        assertFalse(entity.isVerified());
        assertNotNull(entity.getVerificationToken());
        verify(emailService).sendVerificationEmail(eq("a@b.com"), anyString());
    }

    @Test
    void verifyAccount_validToken_verifiesUser() {
        var user = new User();
        user.setVerified(false);
        user.setVerificationToken("tok");

        when(userRepository.findByVerificationToken("tok")).thenReturn(Optional.of(user));

        userService.verifyAccount("tok");

        assertTrue(user.isVerified());
        assertNull(user.getVerificationToken());
        verify(userRepository).save(user);
    }

    @Test
    void verifyAccount_invalidToken_throws() {
        when(userRepository.findByVerificationToken("bad")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.verifyAccount("bad"));
    }
}
