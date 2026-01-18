package com.stream.four.service;

import com.stream.four.dto.response.subscription.TrialResponse;
import com.stream.four.exception.DuplicateResourceException;
import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.model.user.User;
import com.stream.four.model.subscription.Trial;
import com.stream.four.model.enums.TrialStatus;
import com.stream.four.repository.UserRepository;
import com.stream.four.repository.TrialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TrialService {

    private final TrialRepository trialRepository;
    private final UserRepository userRepository;

    /**
     * Create 7-day trial for new user
     * Requirement: "New users receive a seven-day trial period"
     */
    public TrialResponse createTrial(String userId) {
        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if trial already exists
        if (trialRepository.existsByUser_UserId(userId)) {
            throw new DuplicateResourceException("Trial already exists for this user");
        }

        // Create trial
        Trial trial = Trial.builder()
                .user(user)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .status(TrialStatus.ACTIVE)
                .convertedToPaid(false)
                .build();

        Trial savedTrial = trialRepository.save(trial);

        return toTrialResponse(savedTrial);
    }

    /**
     * Get trial for user
     */
    @Transactional(readOnly = true)
    public TrialResponse getTrial(String userId) {
        Trial trial = trialRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No trial found for this user"));

        return toTrialResponse(trial);
    }

    /**
     * Check if trial is active
     */
    @Transactional(readOnly = true)
    public boolean hasActiveTrial(String userId) {
        return trialRepository.findByUser_UserId(userId)
                .map(trial -> trial.getStatus() == TrialStatus.ACTIVE &&
                        trial.getEndDate().isAfter(LocalDate.now()))
                .orElse(false);
    }

    /**
     * Mark trial as converted to paid
     */
    public void markTrialAsConverted(String userId) {
        Trial trial = trialRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No trial found"));

        trial.setStatus(TrialStatus.CONVERTED);
        trial.setConvertedToPaid(true);
        trial.setConvertedDate(LocalDate.now());

        trialRepository.save(trial);
    }

    /**
     * Expire trials (scheduled job)
     */
    public void expireTrials() {
        List<Trial> expiredTrials = trialRepository
                .findByStatusAndEndDateBefore(TrialStatus.ACTIVE, LocalDate.now());

        for (Trial trial : expiredTrials) {
            trial.setStatus(TrialStatus.EXPIRED);
            trialRepository.save(trial);
        }
    }

    // ========== HELPER METHODS ==========

    private TrialResponse toTrialResponse(Trial trial) {
        Integer daysRemaining = null;
        if (trial.getStatus() == TrialStatus.ACTIVE && trial.getEndDate() != null) {
            daysRemaining = (int) ChronoUnit.DAYS.between(LocalDate.now(), trial.getEndDate());
            if (daysRemaining < 0) daysRemaining = 0;
        }

        return TrialResponse.builder()
                .trialId(trial.getTrialId())
                .userId(trial.getUser().getUserId())
                .startDate(trial.getStartDate())
                .endDate(trial.getEndDate())
                .status(trial.getStatus().name())
                .daysRemaining(daysRemaining)
                .convertedToPaid(trial.getConvertedToPaid())
                .convertedDate(trial.getConvertedDate())
                .build();
    }
}