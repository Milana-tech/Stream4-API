package com.stream.four.service;
import com.stream.four.model.User;
import com.stream.four.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordRecoveryService {
    private final UserRepository userRepository;

    public void initiateRecovery(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);

        String link = "https://streamflix.com/reset?token=" + token;
        System.out.println("Link generated: " + link);
    }

    public void completePasswordReset(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        user.setPassword(newPassword);

        user.setResetToken(null);
        user.setFailedLoginAttempts(0);

        userRepository.save(user);
        System.out.println("Password successfully updated for: " + user.getEmail());
    }
}