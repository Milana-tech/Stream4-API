package com.stream.four.repository;

import com.stream.four.model.subscription.Trial;
import com.stream.four.model.enums.TrialStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrialRepository extends JpaRepository<Trial, Long> {

    Optional<Trial> findByUser_UserId(String userId);  // ← Changed

    boolean existsByUser_UserId(String userId);  // ← Changed

    List<Trial> findByStatusAndEndDateBefore(TrialStatus status, LocalDate date);
}