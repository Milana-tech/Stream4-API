package com.stream.four.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stream.four.model.watch.WatchEvent;

public interface WatchEventRepository extends JpaRepository<WatchEvent, String> {

    Optional<WatchEvent> findByUserIdAndTitleId(String userId, String titleId);

    List<WatchEvent> findByUserIdOrderByLastUpdatedDesc(String userId);
}
