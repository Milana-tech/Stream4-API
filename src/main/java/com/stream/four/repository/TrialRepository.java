package com.stream.four.repository;

import com.stream.four.model.Trial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrialRepository extends JpaRepository<Trial, String> {

    Optional<Trial> findByUserId(String userId);
}
