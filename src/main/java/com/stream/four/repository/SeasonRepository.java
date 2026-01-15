package com.stream.four.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stream.four.model.Season;

public interface SeasonRepository extends JpaRepository<Season, String> {
    List<Season> findByTitleIdAndDeletedFalse(String titleId);
}
