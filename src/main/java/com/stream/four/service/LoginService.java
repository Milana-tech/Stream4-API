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
        if (isValidEmail(loginRequest.getLogin())) {
            var email = loginRequest.getLogin();
            var user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User with email" + email + "not found"));
            if (user.getPassword().equals(loginRequest.getPassword())) {
                return user;
            }
        } else {
            var username = loginRequest.getLogin();
            var user = userRepository.findByName(username)
                    .orElseThrow(() -> new IllegalArgumentException("User with username " + username + "not found"));
            if (user.getPassword().equals(loginRequest.getPassword())) {
                return user;
            }
        }
        throw new IllegalArgumentException("Incorrect credentials for the login");
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(regex);
    }
}