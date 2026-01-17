package com.stream.four.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stream.four.model.watch.Preferences;

public interface PreferencesRepository extends JpaRepository<Preferences, String> {
    Optional<Preferences> findByUserId(String userId);
}
