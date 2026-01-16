package com.stream.four.service;

import com.stream.four.model.User;
import com.stream.four.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PasswordRecoveryTest {

    @Autowired
    private PasswordRecoveryService recoveryService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldGenerateTokenWhenRecoveryRequested() {
        User user = new User();
        user.setName("TestUser");
        user.setEmail("test@example.com");
        user.setPassword("oldPassword123");
        userRepository.save(user);

        recoveryService.initiateRecovery("test@example.com");

        User updatedUser = userRepository.findByEmail("test@example.com")
                .orElseThrow(() -> new RuntimeException("User not found"));

        assertNotNull(updatedUser.getResetToken(), "The reset token should not be null");
        assertFalse(updatedUser.getResetToken().isEmpty(), "The token should not be empty");

        System.out.println("Generated Token: " + updatedUser.getResetToken());
    }
}
