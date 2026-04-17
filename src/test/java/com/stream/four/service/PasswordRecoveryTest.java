package com.stream.four.service;

import com.stream.four.model.user.User;
import com.stream.four.repository.UserRepository;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {
    "JWT_SECRET=test-secret-key-for-testing-only-32chars!!",
    "MAIL_USERNAME=test@test.com",
    "MAIL_PASSWORD=testpassword",
    "MYSQL_ROOT_PASSWORD=testroot",
    "EMPLOYEE_JUNIOR_PASSWORD=junior",
    "EMPLOYEE_MID_PASSWORD=mid",
    "EMPLOYEE_SENIOR_PASSWORD=senior",
    "API_USER_PASSWORD=apiuser"
})
class PasswordRecoveryTest {

    @Autowired
    private PasswordRecoveryService recoveryService;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    void shouldGenerateResetTokenWhenRecoveryRequested() {
        User user = new User();
        user.setName("TestUser");
        user.setEmail("test@example.com");
        user.setPassword("oldPassword123");
        userRepository.save(user);

        recoveryService.initiateRecovery("test@example.com");

        User updatedUser = userRepository.findByEmail("test@example.com")
                .orElseThrow();

        assertNotNull(updatedUser.getResetToken());
        assertFalse(updatedUser.getResetToken().isEmpty());
    }

    @Test
    void shouldResetPasswordAndClearToken() {
        User user = new User();
        user.setName("TestUser2");
        user.setEmail("test2@example.com");
        user.setPassword("oldPassword");
        user.setResetToken("valid-token");
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        user.setFailedLoginAttempts(3);
        userRepository.save(user);

        recoveryService.completePasswordReset("valid-token", "newPassword123");

        User updatedUser = userRepository.findByEmail("test2@example.com")
                .orElseThrow();

        assertNull(updatedUser.getResetToken());
        assertEquals(0, updatedUser.getFailedLoginAttempts());
    }
}
