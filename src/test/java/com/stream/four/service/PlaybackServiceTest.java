package com.stream.four.service;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class PlaybackServiceTest {

    private final PlaybackService playbackService = new PlaybackService(
            mock(UserRepository.class),
            mock(TitleRepository.class),
            mock(ProfileRepository.class),
            mock(ContentService.class)
    );

    private User userWithPlan(String plan) {
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
        User user = userWithPlan("UHD");
        Title title = titleWithQualities(VideoQuality.SD, VideoQuality.HD, VideoQuality.UHD);

        // Act
        VideoQuality result = playbackService.getAvailableQuality(user, title);

        // Assert
        assertEquals(VideoQuality.UHD, result);
    }

    @Test
    void uhdPlan_titleOnlySupportsHd_returnsHd() {
        // Arrange
        User user = userWithPlan("UHD");
        Title title = titleWithQualities(VideoQuality.SD, VideoQuality.HD);

        // Act
        VideoQuality result = playbackService.getAvailableQuality(user, title);

        // Assert
        assertEquals(VideoQuality.HD, result);
    }

    @Test
    void hdPlan_titleSupportsUhd_returnsHd() {
        // Arrange
        User user = userWithPlan("HD");
        Title title = titleWithQualities(VideoQuality.SD, VideoQuality.HD, VideoQuality.UHD);

        // Act
        VideoQuality result = playbackService.getAvailableQuality(user, title);

        // Assert
        assertEquals(VideoQuality.HD, result);
    }

    @Test
    void sdPlan_titleSupportsAllQualities_returnsSd() {
        // Arrange
        User user = userWithPlan("SD");
        Title title = titleWithQualities(VideoQuality.SD, VideoQuality.HD, VideoQuality.UHD);

        // Act
        VideoQuality result = playbackService.getAvailableQuality(user, title);

        // Assert
        assertEquals(VideoQuality.SD, result);
    }

    @Test
    void uhdPlan_titleOnlySupportsSd_returnsSd() {
        // Arrange
        User user = userWithPlan("UHD");
        Title title = titleWithQualities(VideoQuality.SD);

        // Act
        VideoQuality result = playbackService.getAvailableQuality(user, title);

        // Assert
        assertEquals(VideoQuality.SD, result);
    }

    @Test
    void noSubscription_returnsSd() {
        // Arrange
        User user = new User();
        Title title = titleWithQualities(VideoQuality.SD, VideoQuality.HD, VideoQuality.UHD);

        // Act
        VideoQuality result = playbackService.getAvailableQuality(user, title);

        // Assert
        assertEquals(VideoQuality.SD, result);
    }
}