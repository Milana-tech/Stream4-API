package com.stream.four.service;

import com.stream.four.dto.requests.CreateUserRequest;
import com.stream.four.dto.response.user.UserResponse;
import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.mapper.UserMapper;
import com.stream.four.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService
{
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

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
        var user = userMapper.toEntity(createUserRequest);
        user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        user.setVerificationToken(UUID.randomUUID().toString());
        user.setVerified(false);
        var saved = userRepository.save(user);
        emailService.sendVerificationEmail(saved.getEmail(), saved.getVerificationToken());
        return userMapper.toDto(saved);
    }

    public void verifyAccount(String token)
    {
        var user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired verification token"));
        user.setVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
    }
}