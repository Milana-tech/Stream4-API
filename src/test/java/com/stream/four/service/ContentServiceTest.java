package com.stream.four.service;

import com.stream.four.model.enums.ContentWarning;
import com.stream.four.model.enums.Genre;
import com.stream.four.model.enums.MaturityRating;
import com.stream.four.model.enums.TitleType;
import com.stream.four.model.user.Profile;
import com.stream.four.model.watch.Preferences;
import com.stream.four.model.watch.Title;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ContentServiceTest {

    private final ContentService contentService = new ContentService();

    private Profile profileWithLevel(String maturityLevel) {
        var profile = new Profile();
        profile.setMaturityLevel(maturityLevel);
        return profile;
    }

    private Title titleWithRating(MaturityRating rating) {
        var title = new Title();
        title.setMaturityRating(rating);
        title.setContentWarnings(Set.of());
        return title;
    }

    // --- Age / Maturity filtering ---

    @Test
    void kidsProfile_allRatedTitle_canWatch() {
        // Arrange
        Profile profile = profileWithLevel("KIDS");
        Title title = titleWithRating(MaturityRating.ALL);

        // Act
        boolean result = contentService.canProfileWatch(profile, title);

        // Assert
        assertTrue(result);
    }

    @Test
    void kidsProfile_teenRatedTitle_cannotWatch() {
        // Arrange
        Profile profile = profileWithLevel("KIDS");
        Title title = titleWithRating(MaturityRating.TEEN);

        // Act
        boolean result = contentService.canProfileWatch(profile, title);

        // Assert
        assertFalse(result);
    }

    @Test
    void teensProfile_teenRatedTitle_canWatch() {
        // Arrange
        Profile profile = profileWithLevel("TEENS");
        Title title = titleWithRating(MaturityRating.TEEN);

        // Act
        boolean result = contentService.canProfileWatch(profile, title);

        // Assert
        assertTrue(result);
    }

    @Test
    void teensProfile_matureRatedTitle_cannotWatch() {
        // Arrange
        Profile profile = profileWithLevel("TEENS");
        Title title = titleWithRating(MaturityRating.MATURE);

        // Act
        boolean result = contentService.canProfileWatch(profile, title);

        // Assert
        assertFalse(result);
    }

    @Test
    void adultProfile_matureRatedTitle_canWatch() {
        // Arrange
        Profile profile = profileWithLevel("ADULT");
        Title title = titleWithRating(MaturityRating.MATURE);

        // Act
        boolean result = contentService.canProfileWatch(profile, title);

        // Assert
        assertTrue(result);
    }

    // --- Content warning filtering ---

    @Test
    void profileWithViolenceFilter_titleHasViolence_cannotWatch() {
        // Arrange
        Profile profile = profileWithLevel("ADULT");
        profile.setContentFilters(List.of(ContentWarning.VIOLENCE));

        Title title = titleWithRating(MaturityRating.MATURE);
        title.setContentWarnings(Set.of(ContentWarning.VIOLENCE));

        // Act
        boolean result = contentService.canProfileWatch(profile, title);

        // Assert
        assertFalse(result);
    }

    @Test
    void profileWithViolenceFilter_titleHasNoViolence_canWatch() {
        // Arrange
        Profile profile = profileWithLevel("ADULT");
        profile.setContentFilters(List.of(ContentWarning.VIOLENCE));

        Title title = titleWithRating(MaturityRating.MATURE);
        title.setContentWarnings(Set.of(ContentWarning.COARSE_LANGUAGE));

        // Act
        boolean result = contentService.canProfileWatch(profile, title);

        // Assert
        assertTrue(result);
    }

    // --- Preferences filtering ---

    @Test
    void filterForProfile_genrePreference_excludesNonMatchingTitles() {
        // Arrange
        Profile profile = profileWithLevel("ADULT");

        Title actionTitle = titleWithRating(MaturityRating.ALL);
        actionTitle.setGenre(Genre.ACTION);

        Title comedyTitle = titleWithRating(MaturityRating.ALL);
        comedyTitle.setGenre(Genre.COMEDY);

        Preferences preferences = new Preferences();
        preferences.setPreferredGenres(Set.of(Genre.ACTION));

        // Act
        List<Title> result = contentService.filterForProfile(List.of(actionTitle, comedyTitle), profile, preferences);

        // Assert
        assertEquals(1, result.size());
        assertEquals(Genre.ACTION, result.get(0).getGenre());
    }

    @Test
    void filterForProfile_typePreference_excludesNonMatchingTitles() {
        // Arrange
        Profile profile = profileWithLevel("ADULT");

        Title movie = titleWithRating(MaturityRating.ALL);
        movie.setType(TitleType.MOVIE);

        Title series = titleWithRating(MaturityRating.ALL);
        series.setType(TitleType.SERIES);

        Preferences preferences = new Preferences();
        preferences.setPreferredType(TitleType.MOVIE);

        // Act
        List<Title> result = contentService.filterForProfile(List.of(movie, series), profile, preferences);

        // Assert
        assertEquals(1, result.size());
        assertEquals(TitleType.MOVIE, result.get(0).getType());
    }

    @Test
    void filterForProfile_minimumMaturityRating_excludesBelowMinimum() {
        // Arrange
        Profile profile = profileWithLevel("ADULT");

        Title allTitle = titleWithRating(MaturityRating.ALL);
        Title matureTitle = titleWithRating(MaturityRating.MATURE);

        Preferences preferences = new Preferences();
        preferences.setMinimumMaturityRating(MaturityRating.MATURE);

        // Act
        List<Title> result = contentService.filterForProfile(List.of(allTitle, matureTitle), profile, preferences);

        // Assert
        assertEquals(1, result.size());
        assertEquals(MaturityRating.MATURE, result.get(0).getMaturityRating());
    }
}
