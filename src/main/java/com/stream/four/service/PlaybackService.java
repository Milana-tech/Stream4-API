package com.stream.four.service;

import com.stream.four.exception.ResourceNotFoundException;
import com.stream.four.exception.UnauthorizedException;
import com.stream.four.model.enums.MaturityRating;
import com.stream.four.model.enums.VideoQuality;
import com.stream.four.model.enums.SubscriptionPlan;
import com.stream.four.model.user.Profile;
import com.stream.four.model.user.User;
import com.stream.four.model.watch.Title;
import com.stream.four.repository.ProfileRepository;
import com.stream.four.repository.TitleRepository;
import com.stream.four.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@Service
public class PlaybackService {

    private final UserRepository userRepository;
    private final TitleRepository titleRepository;
    private final ProfileRepository profileRepository;
    private final ContentService contentService;
    private final TrialService trialService;

    public PlaybackService(UserRepository userRepository, TitleRepository titleRepository,
                           ProfileRepository profileRepository, ContentService contentService,
                           TrialService trialService) {
        this.userRepository = userRepository;
        this.titleRepository = titleRepository;
        this.profileRepository = profileRepository;
        this.contentService = contentService;
        this.trialService = trialService;
    }

    public VideoQuality getAvailableQuality(User user, Title title) {
        boolean hasActiveSub = user.getSubscription() != null && user.getSubscription().isActive();
        boolean hasActiveTrial = trialService.hasActiveTrial(user.getUserId());

        if (!hasActiveSub && !hasActiveTrial) {
            return VideoQuality.SD;
        }

        // Trial users get HD at most
        if (!hasActiveSub) {
            return title.getSupportedQualities().contains(VideoQuality.HD) ? VideoQuality.HD : VideoQuality.SD;
        }

        Set<VideoQuality> supported = title.getSupportedQualities();
        SubscriptionPlan plan = user.getSubscription().getPlan();

        if (plan == SubscriptionPlan.UHD && supported.contains(VideoQuality.UHD)) {
            return VideoQuality.UHD;
        }

        if ((plan == SubscriptionPlan.UHD || plan == SubscriptionPlan.HD) && supported.contains(VideoQuality.HD)) {
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

        boolean hasAccess = (user.getSubscription() != null && user.getSubscription().isActive())
                || trialService.hasActiveTrial(user.getUserId());
        if (!hasAccess) {
            return "User " + email + " has no active subscription or trial. Quality: SD";
        }

        if (!contentService.canProfileWatch(profile, title)) {
            return "ACCESS DENIED: This content is rated " + title.getMaturityRating() +
                    ". Content warnings: " + title.getContentWarnings().stream().map(Enum::name).collect(java.util.stream.Collectors.joining(", "));
        }

        VideoQuality quality = getAvailableQuality(user, title);
        return "User " + email + " is watching " + titleName + " in " + quality.name();
    }
}
