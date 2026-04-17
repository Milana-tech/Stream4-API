package com.stream.four.security;

import com.stream.four.model.enums.Role;
import com.stream.four.model.user.User;
import com.stream.four.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeSecurityTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final EmployeeSecurity employeeSecurity = new EmployeeSecurity(userRepository);

    private void mockUserWithRole(String userId, Role role) {
        var user = new User();
        user.setRole(role);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    }

    // --- canViewBasicInfo ---

    @Test
    void juniorEmployee_canViewBasicInfo() {
        // Arrange
        mockUserWithRole("u1", Role.JUNIOR_EMPLOYEE);

        // Act & Assert
        assertTrue(employeeSecurity.canViewBasicInfo("u1"));
    }

    @Test
    void midEmployee_canViewBasicInfo() {
        // Arrange
        mockUserWithRole("u1", Role.MID_EMPLOYEE);

        // Act & Assert
        assertTrue(employeeSecurity.canViewBasicInfo("u1"));
    }

    @Test
    void seniorEmployee_canViewBasicInfo() {
        // Arrange
        mockUserWithRole("u1", Role.SENIOR_EMPLOYEE);

        // Act & Assert
        assertTrue(employeeSecurity.canViewBasicInfo("u1"));
    }

    @Test
    void regularUser_cannotViewBasicInfo() {
        // Arrange
        mockUserWithRole("u1", Role.USER);

        // Act & Assert
        assertFalse(employeeSecurity.canViewBasicInfo("u1"));
    }

    // --- canModifyProfiles ---

    @Test
    void juniorEmployee_cannotModifyProfiles() {
        // Arrange
        mockUserWithRole("u1", Role.JUNIOR_EMPLOYEE);

        // Act & Assert
        assertFalse(employeeSecurity.canModifyProfiles("u1"));
    }

    @Test
    void midEmployee_canModifyProfiles() {
        // Arrange
        mockUserWithRole("u1", Role.MID_EMPLOYEE);

        // Act & Assert
        assertTrue(employeeSecurity.canModifyProfiles("u1"));
    }

    @Test
    void seniorEmployee_canModifyProfiles() {
        // Arrange
        mockUserWithRole("u1", Role.SENIOR_EMPLOYEE);

        // Act & Assert
        assertTrue(employeeSecurity.canModifyProfiles("u1"));
    }

    // --- canViewFinancialData ---

    @Test
    void juniorEmployee_cannotViewFinancialData() {
        // Arrange
        mockUserWithRole("u1", Role.JUNIOR_EMPLOYEE);

        // Act & Assert
        assertFalse(employeeSecurity.canViewFinancialData("u1"));
    }

    @Test
    void midEmployee_cannotViewFinancialData() {
        // Arrange
        mockUserWithRole("u1", Role.MID_EMPLOYEE);

        // Act & Assert
        assertFalse(employeeSecurity.canViewFinancialData("u1"));
    }

    @Test
    void seniorEmployee_canViewFinancialData() {
        // Arrange
        mockUserWithRole("u1", Role.SENIOR_EMPLOYEE);

        // Act & Assert
        assertTrue(employeeSecurity.canViewFinancialData("u1"));
    }
}