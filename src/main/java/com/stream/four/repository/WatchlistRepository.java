package com.stream.four.repository;

import com.stream.four.model.watch.WatchlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatchlistRepository extends JpaRepository<WatchlistItem, String> {

    List<WatchlistItem> findByUserIdOrderByAddedAtDesc(String userId);

    List<WatchlistItem> findByUserIdAndProfileIdOrderByAddedAtDesc(String userId, String profileId);

    boolean existsByUserIdAndTitleId(String userId, String titleId);

    boolean existsByUserIdAndProfileIdAndTitleId(String userId, String profileId, String titleId);

    void deleteByUserIdAndTitleId(String userId, String titleId);

    void deleteByUserIdAndProfileIdAndTitleId(String userId, String profileId, String titleId);
}
