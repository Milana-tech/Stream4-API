package com.stream.four.service;

import com.stream.four.dto.response.subscription.SubscriptionOverviewResponse;
import com.stream.four.dto.response.user.EmployeeBasicInfoResponse;
import com.stream.four.dto.response.user.ProfileStatusResponse;
import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.model.enums.SubscriptionStatus;
import com.stream.four.repository.ProfileRepository;
import com.stream.four.repository.SubscriptionRepository;
import com.stream.four.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final SubscriptionRepository subscriptionRepository;

    public EmployeeBasicInfoResponse getBasicInfo(String userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return new EmployeeBasicInfoResponse(user.getUserId(), user.getName(), user.getEmail(), user.getRole().name());
    }

    public ProfileStatusResponse activateProfile(String profileId) {
        var profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        profile.setDeleted(false);
        profileRepository.save(profile);
        return new ProfileStatusResponse(profileId, "ACTIVE");
    }

    public ProfileStatusResponse deactivateProfile(String profileId) {
        var profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        profile.setDeleted(true);
        profileRepository.save(profile);
        return new ProfileStatusResponse(profileId, "DEACTIVATED");
    }

    public SubscriptionOverviewResponse getSubscriptionOverview() {
        var all = subscriptionRepository.findAll();
        long activeCount = all.stream()
                .filter(s -> s.getStatus() == SubscriptionStatus.ACTIVE)
                .count();
        BigDecimal totalRevenue = all.stream()
                .filter(s -> s.getStatus() == SubscriptionStatus.ACTIVE)
                .map(s -> s.getTotalPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String summary = "Active subscriptions: " + activeCount + " | Monthly revenue: \u20ac" + totalRevenue;
        String history = "Total subscriptions on record: " + all.size();
        return new SubscriptionOverviewResponse(summary, history);
    }
}
