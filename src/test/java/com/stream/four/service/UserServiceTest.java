package com.stream.four.service;

import com.stream.four.dto.requests.CreateUserRequest;
import com.stream.four.dto.requests.UpdateUserRequest;
import com.stream.four.dto.response.user.UserResponse;
import com.stream.four.exception.DuplicateResourceException;
import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.mapper.UserMapper;
import com.stream.four.model.user.User;
import com.stream.four.repository.InvitationRepository;
import com.stream.four.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
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
    private final InvitationRepository invitationRepository = mock(InvitationRepository.class);

    private final UserService userService = new UserService(userRepository, userMapper, emailService, passwordEncoder, trialService, invitationRepository);

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
        assertThrows(com.stream.four.exception.ResourceNotFoundException.class, () -> userService.getUser("missing"));
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

    @Test
    void verifyAccount_expiredToken_throws() {
        var user = new User();
        user.setVerificationToken("tok");
        user.setVerificationTokenExpiry(LocalDateTime.now().minusHours(1));

        when(userRepository.findByVerificationToken("tok")).thenReturn(Optional.of(user));

        assertThrows(ResourceNotFoundException.class, () -> userService.verifyAccount("tok"));
        assertNull(user.getVerificationToken());
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_name_updatesAndReturnsDto() {
        var user = new User();
        user.setName("Old");
        user.setEmail("a@b.com");
        var dto = new UserResponse();

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(dto);

        var req = new UpdateUserRequest();
        req.setName("New");

        var result = userService.updateUser("u1", req);

        assertSame(dto, result);
        assertEquals("New", user.getName());
    }

    @Test
    void updateUser_duplicateEmail_throws() {
        var user = new User();
        user.setEmail("old@b.com");

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("taken@b.com")).thenReturn(true);

        var req = new UpdateUserRequest();
        req.setEmail("taken@b.com");

        assertThrows(DuplicateResourceException.class, () -> userService.updateUser("u1", req));
    }

    @Test
    void updateUser_notFound_throws() {
        when(userRepository.findById("missing")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUser("missing", new UpdateUserRequest()));
    }
}
