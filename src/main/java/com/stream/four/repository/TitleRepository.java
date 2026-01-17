package com.stream.four.repository;

import java.util.List;
import java.util.Optional; // Add this import
import org.springframework.data.jpa.repository.JpaRepository;
import com.stream.four.model.Title;

public interface TitleRepository extends JpaRepository<Title, String> {
    List<Title> findByDeletedFalse();

    Optional<Title> findByName(String name);
}