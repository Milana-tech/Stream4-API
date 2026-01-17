package com.stream.four.model.subscription;

import com.stream.four.model.enums.TrialStatus;
import com.stream.four.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Trial")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TrialID")
    private Long trialId;

    @OneToOne
    @JoinColumn(name = "UserID", nullable = false, unique = true)  // ← Changed
    private User user;  // ← Changed

    @Column(name = "StartDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "EndDate", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private TrialStatus status;

    @Column(name = "ConvertedToPaid")
    private Boolean convertedToPaid = false;

    @Column(name = "ConvertedDate")
    private LocalDate convertedDate;

    @CreationTimestamp
    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.startDate == null) {
            this.startDate = LocalDate.now();
        }
        if (this.endDate == null) {
            this.endDate = this.startDate.plusDays(7);
        }
        if (this.status == null) {
            this.status = TrialStatus.ACTIVE;
        }
    }
}