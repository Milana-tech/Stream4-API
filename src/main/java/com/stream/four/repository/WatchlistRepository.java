package com.stream.four.repository;

import com.stream.four.model.watch.WatchlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatchlistRepository extends JpaRepository<WatchlistItem, String> {

    List<WatchlistItem> findByUserIdOrderByAddedAtDesc(String userId);

    boolean existsByUserIdAndTitleId(String userId, String titleId);
}
