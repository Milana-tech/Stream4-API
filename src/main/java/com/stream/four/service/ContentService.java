package com.stream.four.service;

import com.stream.four.model.enums.ContentWarning;
import com.stream.four.model.enums.MaturityRating;
import com.stream.four.model.user.Profile;
import com.stream.four.model.watch.Preferences;
import com.stream.four.model.watch.Title;
import com.stream.four.model.enums.VideoQuality;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentService
{
    public boolean canProfileWatch(Profile profile, Title title) {
        if (!isAgeAllowed(profile.getMaturityLevel(), title.getMaturityRating())) {
            return false;
        }

        if (profile.getContentFilters() != null) {
            for (ContentWarning filter : profile.getContentFilters()) {
                if (title.getContentWarnings().contains(filter)) {
                    return false;
                }
            }
        }
        return true;
    }

    public String getAvailableQuality(Title title, String requestedQuality) {
        if (title.getSupportedQualities() == null) {
            return "SD";
        }

        try {
            VideoQuality requestedEnum = VideoQuality.valueOf(requestedQuality.toUpperCase());

            if (title.getSupportedQualities().contains(requestedEnum)) {
                return requestedEnum.name();
            }
        } catch (IllegalArgumentException e) {
            return "SD";
        }

        return "SD";
    }

    public List<Title> filterForProfile(List<Title> titles, Profile profile, Preferences preferences) {
        return titles.stream()
                .filter(title -> canProfileWatch(profile, title))
                .filter(title -> preferences == null || matchesPreferences(title, preferences))
                .toList();
    }

    private boolean matchesPreferences(Title title, Preferences preferences) {
        if (preferences.getPreferredType() != null && title.getType() != preferences.getPreferredType()) {
            return false;
        }
        if (preferences.getPreferredGenres() != null && !preferences.getPreferredGenres().isEmpty()) {
            if (!preferences.getPreferredGenres().contains(title.getGenre())) {
                return false;
            }
        }
        if (preferences.getMinimumMaturityRating() != null && title.getMaturityRating() != null) {
            if (title.getMaturityRating().ordinal() < preferences.getMinimumMaturityRating().ordinal()) {
                return false;
            }
        }
        return true;
    }

    private boolean isAgeAllowed(String profileLevel, MaturityRating titleRating) {
        if (titleRating == null) return true;
        if (profileLevel == null) return false;

        return switch (profileLevel.toUpperCase()) {
            case "KIDS"  -> titleRating == MaturityRating.ALL;
            case "TEENS" -> titleRating == MaturityRating.ALL || titleRating == MaturityRating.PG || titleRating == MaturityRating.TEEN;
            case "ADULT" -> true;
            default      -> false;
        };
    }
}
