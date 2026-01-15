package com.stream.four.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stream.four.model.Title;

public interface TitleRepository extends JpaRepository<Title, String> {
    List<Title> findByDeletedFalse();
}
