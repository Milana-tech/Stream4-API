package com.stream.four.repository;

import com.stream.four.model.Trial;
import com.stream.four.model.TrialStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrialRepository extends JpaRepository<Trial, Long> {

    Optional<Trial> findByUser_Id(String userId);  // ← Changed

    boolean existsByUser_Id(String userId);  // ← Changed

    List<Trial> findByStatusAndEndDateBefore(TrialStatus status, LocalDate date);
}