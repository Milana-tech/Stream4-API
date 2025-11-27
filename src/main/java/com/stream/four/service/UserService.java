package com.stream.four.service;

import com.stream.four.dto.CreateUserRequest;
import com.stream.four.dto.UserResponse;
import com.stream.four.mapper.UserMapper;
import com.stream.four.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService
{
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponse> getAllUsers()
    {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserResponse getUser(String userId)
    {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id" + userId + " wasn't found"));
        return userMapper.toDto(user);
    }

    public UserResponse createUser(CreateUserRequest createUserRequest)
    {
        var user = userRepository.save(userMapper.toEntity(createUserRequest));
        return userMapper.toDto(user);
    }
}