package com.stream.four.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stream.four.model.watch.WatchEvent;

public interface WatchEventRepository extends JpaRepository<WatchEvent, String> {

    Optional<WatchEvent> findByUserIdAndTitleId(String userId, String titleId);

    Optional<WatchEvent> findByUserIdAndProfileIdAndTitleId(String userId, String profileId, String titleId);

    Optional<WatchEvent> findByUserIdAndTitleIdAndEpisodeId(String userId, String titleId, String episodeId);

    Optional<WatchEvent> findByUserIdAndProfileIdAndTitleIdAndEpisodeId(String userId, String profileId, String titleId, String episodeId);

    List<WatchEvent> findByUserIdOrderByLastUpdatedDesc(String userId);

    List<WatchEvent> findByUserIdAndProfileIdOrderByLastUpdatedDesc(String userId, String profileId);
}
