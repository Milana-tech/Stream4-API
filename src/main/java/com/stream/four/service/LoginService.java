package com.stream.four.service;

import com.stream.four.dto.response.user.LoginRequest;
import com.stream.four.model.user.User;
import com.stream.four.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User login(LoginRequest loginRequest) {
        String identifier = loginRequest.getLogin();
        User user;

        if (isValidEmail(identifier)) {
            user = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid credentials."));
        } else {
            user = userRepository.findByName(identifier)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid credentials."));
        }

        if (!user.isVerified()) {
            throw new IllegalStateException("Account is not verified. Please check your email for the activation link.");
        }

        if (user.getFailedLoginAttempts() >= 3) {
            throw new IllegalStateException("Account is temporarily blocked due to 3 failed attempts.");
        }

        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            user.setFailedLoginAttempts(0);
            userRepository.save(user);
            return user;
        } else {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            userRepository.save(user);

            if (user.getFailedLoginAttempts() >= 3) {
                throw new IllegalStateException("Incorrect password. Account has been blocked.");
            }
            throw new IllegalArgumentException("Invalid credentials.");
        }
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }
}