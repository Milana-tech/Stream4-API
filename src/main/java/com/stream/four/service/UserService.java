package com.stream.four.service;

import com.stream.four.dto.requests.CreateUserRequest;
import com.stream.four.dto.requests.UpdateUserRequest;
import com.stream.four.dto.response.user.UserResponse;
import com.stream.four.exception.DuplicateResourceException;
import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.mapper.UserMapper;
import com.stream.four.repository.InvitationRepository;
import com.stream.four.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService
{
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final TrialService trialService;
    private final InvitationRepository invitationRepository;

    @Value("${app.verification.link-expiry-hours:24}")
    private int linkExpiryHours;

    public UserService(UserRepository userRepository, UserMapper userMapper, EmailService emailService,
                       PasswordEncoder passwordEncoder, TrialService trialService,
                       InvitationRepository invitationRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.trialService = trialService;
        this.invitationRepository = invitationRepository;
    }

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
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toDto(user);
    }

    public UserResponse createUser(CreateUserRequest createUserRequest)
    {
        var user = userMapper.toEntity(createUserRequest);
        user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        user.setVerificationToken(UUID.randomUUID().toString());
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(linkExpiryHours));
        user.setVerified(false);
        var saved = userRepository.save(user);
        emailService.sendVerificationEmail(saved.getEmail(), saved.getVerificationToken());
        trialService.createTrial(saved.getUserId());

        if (createUserRequest.getInvitationToken() != null) {
            invitationRepository.findByToken(createUserRequest.getInvitationToken())
                    .ifPresent(invitation -> {
                        invitation.setInviteeUserId(saved.getUserId());
                        invitationRepository.save(invitation);
                        saved.setInvitedBy(invitation.getInviterUserId());
                        userRepository.save(saved);
                    });
        }

        return userMapper.toDto(saved);
    }

    public void verifyAccount(String token)
    {
        var user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired verification token"));

        if (user.getVerificationTokenExpiry() != null
                && user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            user.setVerificationToken(null);
            user.setVerificationTokenExpiry(null);
            userRepository.save(user);
            throw new ResourceNotFoundException("Verification link has expired. Please register again.");
        }

        user.setVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
    }

    public UserResponse updateUser(String userId, UpdateUserRequest request)
    {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Email is already in use");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        return userMapper.toDto(userRepository.save(user));
    }
}