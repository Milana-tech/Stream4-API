package com.stream.four.service;

import com.stream.four.model.MaturityRating;
import com.stream.four.model.Title;
import com.stream.four.model.User;
import com.stream.four.model.VideoQuality;
import com.stream.four.repository.TitleRepository;
import com.stream.four.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
public class PlaybackService
{

    private final UserRepository userRepository;
    private final TitleRepository titleRepository;
    private final TrialService trialService;

    public PlaybackService(TitleRepository titleRepository, UserRepository userRepository, TrialService trialService)
    {
        this.titleRepository = titleRepository;
        this.userRepository = userRepository;
        this.trialService = trialService;
    }

    public VideoQuality getAvailableQuality(User user, Title title)
    {
        if (user.getSubscription() == null || !user.getSubscription().isActive())
        {
            return VideoQuality.SD;
        }

        Set<VideoQuality> supported = title.getSupportedQualities();
        String plan = user.getSubscription().getPlan().toUpperCase();

        if (plan.equals("PREMIUM") && supported.contains(VideoQuality.UHD))
        {
            return VideoQuality.UHD;
        }

        if ((plan.equals("PREMIUM") || plan.equals("STANDARD")) && supported.contains(VideoQuality.HD))
        {
            return VideoQuality.HD;
        }

        return VideoQuality.SD;
    }

    public String getPlaybackQuality(String email, String titleName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Title title = titleRepository.findByName(titleName)
                .orElseThrow(() -> new RuntimeException("Title not found"));

        // Calculate status first
        boolean hasActiveSub = user.getSubscription() != null && user.getSubscription().isActive();
        boolean hasValidTrial = trialService.hasActiveTrial(user.getId());

        // If no sub, try to start/check trial
        if (!hasActiveSub && !hasValidTrial) {
            try {
                trialService.createTrial(user.getId()); // This creates the 7-day row
                hasValidTrial = true;
            } catch (RuntimeException e) {
                return "User " + email + " has no active subscription or trial eligibility. Quality: SD";
            }
        }

        // Age check
        if (!isAgeAppropriate(user.getAge(), title.getMaturityRating())) {
            return "ACCESS DENIED: Content rated " + title.getMaturityRating();
        }

        // Determine Quality (Trial users get PREMIUM/UHD access)
        String plan = hasActiveSub ? user.getSubscription().getPlan().toUpperCase() : "PREMIUM";

        if (plan.equals("PREMIUM") && title.getSupportedQualities().contains(VideoQuality.UHD)) {
            String message = hasValidTrial && !hasActiveSub ? " (TRIAL ACCESS)" : "";
            return "User " + email + " is watching " + titleName + " in UHD" + message;
        }

        return "User " + email + " is watching " + titleName + " in SD";
    }

    private boolean isAgeAppropriate(int userAge, MaturityRating rating) {
        return userAge >= rating.getMinAge();
    }

    public String validatePlayback(String email, String titleName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Title title = titleRepository.findByName(titleName)
                .orElseThrow(() -> new RuntimeException("Title not found"));

        if (user.isEligibleForTrial()) {
            return "TRIAL ACCESS GRANTED: Enjoy your first 7 days!";
        }

        if (user.getAge() < title.getMaturityRating().getMinAge()) {
            return "ACCESS DENIED: Content is rated " + title.getMaturityRating();
        }

        return "User is watching " + titleName;
    }
}