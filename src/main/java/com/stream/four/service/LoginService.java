package com.stream.four.service;

import com.stream.four.dto.LoginRequest;
import com.stream.four.model.User;
import com.stream.four.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoginService {

    private final UserRepository userRepository;

    public User login(LoginRequest loginRequest) {
        String identifier = loginRequest.getLogin();
        User user;

        if (isValidEmail(identifier)) {
            user = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new IllegalArgumentException("User with email " + identifier + " not found"));
        } else {
            user = userRepository.findByName(identifier)
                    .orElseThrow(() -> new IllegalArgumentException("User with username " + identifier + " not found"));
        }

        if (user.getFailedLoginAttempts() >= 3) {
            throw new IllegalStateException("Account is temporarily blocked due to 3 failed attempts.");
        }

        if (user.getPassword().equals(loginRequest.getPassword())) {
            user.setFailedLoginAttempts(0);
            userRepository.save(user);
            return user;
        } else {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            userRepository.save(user);

            if (user.getFailedLoginAttempts() >= 3) {
                throw new IllegalStateException("Incorrect password. Account has been blocked.");
            }
            throw new IllegalArgumentException("Incorrect credentials. Attempt " + user.getFailedLoginAttempts() + " of 3.");
        }
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(regex);
    }
}