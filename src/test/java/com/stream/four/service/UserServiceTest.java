package com.stream.four.service;

import com.stream.four.dto.requests.CreateUserRequest;
import com.stream.four.dto.response.user.UserResponse;
import com.stream.four.mapper.UserMapper;
import com.stream.four.model.user.User;
import com.stream.four.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserMapper userMapper = mock(UserMapper.class);

    private final UserService userService = new UserService(userRepository, userMapper);

    @Test
    void getAllUsers_mapsEntitiesToDtos() {
        var u1 = new User();
        u1.setId("1");
        var u2 = new User();
        u2.setId("2");

        when(userRepository.findAll()).thenReturn(List.of(u1, u2));
        when(userMapper.toDto(u1)).thenReturn(new UserResponse());
        when(userMapper.toDto(u2)).thenReturn(new UserResponse());

        var result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
        verify(userMapper).toDto(u1);
        verify(userMapper).toDto(u2);
    }

    @Test
    void getUser_existing_returnsDto() {
        var user = new User();
        user.setId("u");

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
    void createUser_savesMappedEntity_andReturnsDto() {
        var req = new CreateUserRequest();
        var entity = new User();
        var saved = new User();
        var dto = new UserResponse();

        when(userMapper.toEntity(req)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(saved);
        when(userMapper.toDto(saved)).thenReturn(dto);

        assertSame(dto, userService.createUser(req));
        verify(userRepository).save(entity);
    }
}

