package com.stream.four.service;

import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.model.enums.Role;
import com.stream.four.model.enums.SubscriptionStatus;
import com.stream.four.model.subscription.Subscription;
import com.stream.four.model.user.Profile;
import com.stream.four.model.user.User;
import com.stream.four.repository.ProfileRepository;
import com.stream.four.repository.SubscriptionRepository;
import com.stream.four.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final ProfileRepository profileRepository = mock(ProfileRepository.class);
    private final SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);

    private final EmployeeService employeeService =
            new EmployeeService(userRepository, profileRepository, subscriptionRepository);

    // ========== getBasicInfo ==========

    @Test
    void getBasicInfo_existingUser_returnsBasicInfoResponse() {
        var user = new User();
        user.setUserId("u1");
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setRole(Role.USER);

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        var result = employeeService.getBasicInfo("u1");

        assertNotNull(result);
        assertEquals("u1", result.getUserId());
        assertEquals("Alice", result.getName());
        assertEquals("alice@example.com", result.getEmail());
        assertEquals("USER", result.getRole());
    }

    @Test
    void getBasicInfo_userNotFound_throwsResourceNotFoundException() {
        when(userRepository.findById("u1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.getBasicInfo("u1"));
    }

    // ========== activateProfile ==========

    @Test
    void activateProfile_existingProfile_setsDeletedFalseAndReturnsActive() {
        var profile = new Profile();
        profile.setId("p1");
        profile.setDeleted(true);

        when(profileRepository.findById("p1")).thenReturn(Optional.of(profile));
        when(profileRepository.save(profile)).thenReturn(profile);

        var result = employeeService.activateProfile("p1");

        assertFalse(profile.isDeleted());
        assertEquals("p1", result.getProfileId());
        assertEquals("ACTIVE", result.getStatus());
        verify(profileRepository).save(profile);
    }

    @Test
    void activateProfile_profileNotFound_throwsResourceNotFoundException() {
        when(profileRepository.findById("p1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.activateProfile("p1"));
    }

    // ========== deactivateProfile ==========

    @Test
    void deactivateProfile_existingProfile_setsDeletedTrueAndReturnsDeactivated() {
        var profile = new Profile();
        profile.setId("p1");
        profile.setDeleted(false);

        when(profileRepository.findById("p1")).thenReturn(Optional.of(profile));
        when(profileRepository.save(profile)).thenReturn(profile);

        var result = employeeService.deactivateProfile("p1");

        assertTrue(profile.isDeleted());
        assertEquals("p1", result.getProfileId());
        assertEquals("DEACTIVATED", result.getStatus());
        verify(profileRepository).save(profile);
    }

    @Test
    void deactivateProfile_profileNotFound_throwsResourceNotFoundException() {
        when(profileRepository.findById("p1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.deactivateProfile("p1"));
    }

    // ========== getSubscriptionOverview ==========

    @Test
    void getSubscriptionOverview_mixedSubscriptions_returnsCorrectActiveCountAndRevenue() {
        var user = new User();
        user.setUserId("u1");

        var active1 = new Subscription();
        active1.setStatus(SubscriptionStatus.ACTIVE);
        active1.setTotalPrice(new BigDecimal("9.99"));
        active1.setUser(user);

        var active2 = new Subscription();
        active2.setStatus(SubscriptionStatus.ACTIVE);
        active2.setTotalPrice(new BigDecimal("14.99"));
        active2.setUser(user);

        var cancelled = new Subscription();
        cancelled.setStatus(SubscriptionStatus.CANCELLED);
        cancelled.setTotalPrice(new BigDecimal("9.99"));
        cancelled.setUser(user);

        when(subscriptionRepository.findAll()).thenReturn(List.of(active1, active2, cancelled));

        var result = employeeService.getSubscriptionOverview();

        assertNotNull(result);
        assertTrue(result.getSubscriptionSummary().contains("Active subscriptions: 2"));
        assertTrue(result.getSubscriptionSummary().contains("24.98"));
        assertTrue(result.getPaymentHistory().contains("Total subscriptions on record: 3"));
    }

    @Test
    void getSubscriptionOverview_noSubscriptions_returnsZeroCounts() {
        when(subscriptionRepository.findAll()).thenReturn(List.of());

        var result = employeeService.getSubscriptionOverview();

        assertTrue(result.getSubscriptionSummary().contains("Active subscriptions: 0"));
        assertTrue(result.getPaymentHistory().contains("Total subscriptions on record: 0"));
    }
}
