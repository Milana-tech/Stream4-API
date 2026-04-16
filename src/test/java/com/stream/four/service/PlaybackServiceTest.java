package com.stream.four.service;

import com.stream.four.model.enums.SubscriptionPlan;
import com.stream.four.model.enums.SubscriptionStatus;
import com.stream.four.model.enums.VideoQuality;
import com.stream.four.model.subscription.Subscription;
import com.stream.four.model.user.User;
import com.stream.four.model.watch.Title;
import com.stream.four.repository.ProfileRepository;
import com.stream.four.repository.TitleRepository;
import com.stream.four.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlaybackServiceTest {

    private final PlaybackService playbackService = new PlaybackService(
            mock(UserRepository.class),
            mock(TitleRepository.class),
            mock(ProfileRepository.class),
            mock(ContentService.class)
    );

    private User userWithPlan(SubscriptionPlan plan) {
        var sub = new Subscription();
        sub.setPlan(plan);
        sub.setStatus(SubscriptionStatus.ACTIVE);
        var user = new User();
        user.setSubscription(sub);
        return user;
    }

    private Title titleWithQualities(VideoQuality... qualities) {
        var title = new Title();
        title.setSupportedQualities(Set.of(qualities));
        return title;
    }

    @Test
    void uhdPlan_titleSupportsUhd_returnsUhd() {
        // Arrange
        User user = userWithPlan(SubscriptionPlan.UHD);
        Title title = titleWithQualities(VideoQuality.SD, VideoQuality.HD, VideoQuality.UHD);

        // Act
        VideoQuality result = playbackService.getAvailableQuality(user, title);

        // Assert
        assertEquals(VideoQuality.UHD, result);
    }

    @Test
    void uhdPlan_titleOnlySupportsHd_returnsHd() {
        // Arrange
        User user = userWithPlan(SubscriptionPlan.UHD);
        Title title = titleWithQualities(VideoQuality.SD, VideoQuality.HD);

        // Act
        VideoQuality result = playbackService.getAvailableQuality(user, title);

        // Assert
        assertEquals(VideoQuality.HD, result);
    }

    @Test
    void hdPlan_titleSupportsUhd_returnsHd() {
        // Arrange
        User user = userWithPlan(SubscriptionPlan.HD);
        Title title = titleWithQualities(VideoQuality.SD, VideoQuality.HD, VideoQuality.UHD);

        // Act
        VideoQuality result = playbackService.getAvailableQuality(user, title);

        // Assert
        assertEquals(VideoQuality.HD, result);
    }

    @Test
    void sdPlan_titleSupportsAllQualities_returnsSd() {
        // Arrange
        User user = userWithPlan(SubscriptionPlan.SD);
        Title title = titleWithQualities(VideoQuality.SD, VideoQuality.HD, VideoQuality.UHD);

        // Act
        VideoQuality result = playbackService.getAvailableQuality(user, title);

        // Assert
        assertEquals(VideoQuality.SD, result);
    }

    @Test
    void uhdPlan_titleOnlySupportsSd_returnsSd() {
        // Arrange
        User user = userWithPlan(SubscriptionPlan.UHD);
        Title title = titleWithQualities(VideoQuality.SD);

        // Act
        VideoQuality result = playbackService.getAvailableQuality(user, title);

        // Assert
        assertEquals(VideoQuality.SD, result);
    }

    @Test
    void noSubscription_returnsSd() {
        User user = new User();
        Title title = titleWithQualities(VideoQuality.SD, VideoQuality.HD, VideoQuality.UHD);
        assertEquals(VideoQuality.SD, playbackService.getAvailableQuality(user, title));
    }

    // --- getPlaybackQuality ---

    @Test
    void getPlaybackQuality_userNotFound_throws() {
        var userRepo = mock(UserRepository.class);
        var service = new PlaybackService(userRepo, mock(TitleRepository.class),
                mock(ProfileRepository.class), mock(ContentService.class));
        when(userRepo.findByEmail("x@x.com")).thenReturn(java.util.Optional.empty());
        assertThrows(com.stream.four.exception.ResourceNotFoundException.class,
                () -> service.getPlaybackQuality("x@x.com", "title", "p1"));
    }

    @Test
    void getPlaybackQuality_titleNotFound_throws() {
        var userRepo = mock(UserRepository.class);
        var titleRepo = mock(TitleRepository.class);
        var service = new PlaybackService(userRepo, titleRepo,
                mock(ProfileRepository.class), mock(ContentService.class));
        when(userRepo.findByEmail("a@a.com")).thenReturn(java.util.Optional.of(new User()));
        when(titleRepo.findByName("missing")).thenReturn(java.util.Optional.empty());
        assertThrows(com.stream.four.exception.ResourceNotFoundException.class,
                () -> service.getPlaybackQuality("a@a.com", "missing", "p1"));
    }

    @Test
    void getPlaybackQuality_noSubscription_returnsSd() {
        var userRepo = mock(UserRepository.class);
        var titleRepo = mock(TitleRepository.class);
        var profileRepo = mock(ProfileRepository.class);
        var service = new PlaybackService(userRepo, titleRepo, profileRepo, mock(ContentService.class));

        var user = new User();
        var title = titleWithQualities(VideoQuality.HD);
        title.setName("Test");
        var profile = new com.stream.four.model.user.Profile();

        when(userRepo.findByEmail("a@a.com")).thenReturn(java.util.Optional.of(user));
        when(titleRepo.findByName("Test")).thenReturn(java.util.Optional.of(title));
        when(profileRepo.findById("p1")).thenReturn(java.util.Optional.of(profile));

        var result = service.getPlaybackQuality("a@a.com", "Test", "p1");
        assertTrue(result.contains("SD"));
    }
}