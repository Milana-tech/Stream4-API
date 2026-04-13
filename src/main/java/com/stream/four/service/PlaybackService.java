package com.stream.four.service;

import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.exception.UnauthorizedException;
import com.stream.four.model.enums.MaturityRating;
import com.stream.four.model.enums.VideoQuality;
import com.stream.four.model.user.Profile;
import com.stream.four.model.user.User;
import com.stream.four.model.watch.Title;
import com.stream.four.repository.ProfileRepository;
import com.stream.four.repository.TitleRepository;
import com.stream.four.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PlaybackService {

    private final UserRepository userRepository;
    private final TitleRepository titleRepository;
    private final ProfileRepository profileRepository;
    private final ContentService contentService;

    public PlaybackService(UserRepository userRepository, TitleRepository titleRepository,
                           ProfileRepository profileRepository, ContentService contentService) {
        this.userRepository = userRepository;
        this.titleRepository = titleRepository;
        this.profileRepository = profileRepository;
        this.contentService = contentService;
    }

    public VideoQuality getAvailableQuality(User user, Title title) {
        if (user.getSubscription() == null || !user.getSubscription().isActive()) {
            return VideoQuality.SD;
        }

        Set<VideoQuality> supported = title.getSupportedQualities();
        String plan = user.getSubscription().getPlan().toUpperCase();

        if (plan.equals("PREMIUM") && supported.contains(VideoQuality.UHD)) {
            return VideoQuality.UHD;
        }

        if ((plan.equals("PREMIUM") || plan.equals("STANDARD")) && supported.contains(VideoQuality.HD)) {
            return VideoQuality.HD;
        }

        return VideoQuality.SD;
    }

    public String getPlaybackQuality(String email, String titleName, String profileId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Title title = titleRepository.findByName(titleName)
                .orElseThrow(() -> new ResourceNotFoundException("Title not found"));

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        if (user.getSubscription() == null || !user.getSubscription().isActive()) {
            return "User " + email + " has no active subscription. Quality: SD";
        }

        if (!contentService.canProfileWatch(profile, title)) {
            return "ACCESS DENIED: This content is rated " + title.getMaturityRating() +
                    ". Content warnings: " + title.getContentWarnings().stream().map(Enum::name).collect(java.util.stream.Collectors.joining(", "));
        }

        String plan = user.getSubscription().getPlan().toUpperCase();

        if (plan.equals("PREMIUM") && title.getSupportedQualities().contains(VideoQuality.UHD)) {
            return "User " + email + " is watching " + titleName + " in UHD";
        }

        return "User " + email + " is watching " + titleName + " in SD";
    }
}
