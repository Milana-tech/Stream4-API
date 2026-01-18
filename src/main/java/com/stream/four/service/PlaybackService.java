package com.stream.four.service;

import com.stream.four.model.enums.MaturityRating;
import com.stream.four.model.watch.Title;
import com.stream.four.model.user.User;
import com.stream.four.model.enums.VideoQuality;
import com.stream.four.repository.TitleRepository;
import com.stream.four.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
public class PlaybackService
{

    private final UserRepository userRepository;
    private final TitleRepository titleRepository;

    public PlaybackService(UserRepository userRepository, TitleRepository titleRepository)
    {
        this.userRepository = userRepository;
        this.titleRepository = titleRepository;
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

    public String getPlaybackQuality(String email, String titleName)
    {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Title title = titleRepository.findByName(titleName)
                .orElseThrow(() -> new RuntimeException("Title not found"));

        if (user.getSubscription() == null || !user.getSubscription().isActive())
        {
            return "User " + email + " has no active subscription. Quality: SD";
        }

        if (!isAgeAppropriate(user.getAge(), title.getMaturityRating()))
        {
            return "ACCESS DENIED: This content is rated " + title.getMaturityRating() +
                    ". Content warnings: " + String.join(", ", title.getContentWarnings());
        }

        String plan = user.getSubscription().getPlan().toUpperCase();

        if (plan.equals("PREMIUM") && title.getSupportedQualities().contains(VideoQuality.UHD))
        {
            return "User " + email + " is watching " + titleName + " in UHD";
        }

        return "User " + email + " is watching " + titleName + " in SD";
    }

    private boolean isAgeAppropriate(int userAge, MaturityRating rating)
    {
        return switch (rating)
        {
            case ALL -> true;
            case PG -> userAge >= 7;
            case TEEN -> userAge >= 13;
            case MATURE -> userAge >= 18;
        };
    }
}