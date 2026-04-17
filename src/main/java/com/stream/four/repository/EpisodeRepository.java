package com.stream.four.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stream.four.model.watch.Episode;

public interface EpisodeRepository extends JpaRepository<Episode, String> {
    List<Episode> findBySeasonIdAndDeletedFalse(String seasonId);
}
