package com.stream.four.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrialResponse {

    private Long trialId;
    private String userId;  // ← Changed
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Integer daysRemaining;
    private Boolean convertedToPaid;
    private LocalDate convertedDate;
}