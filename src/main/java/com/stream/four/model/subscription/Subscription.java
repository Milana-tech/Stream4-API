package com.stream.four.model.subscription;

import com.stream.four.model.user.User;
import com.stream.four.model.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SubscriptionID")
    private Long subscriptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private SubscriptionStatus status;

    @Column(name = "TotalPrice", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "DiscountPercentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Column(name = "DiscountEndDate")
    private LocalDate discountEndDate;

    @Column(name = "StartDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "EndDate")
    private LocalDate endDate;

    @Column(name = "AutoRenew", nullable = false)
    private Boolean autoRenew = true;

    @CreationTimestamp
    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User createdByUser;

    @UpdateTimestamp
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist()
    {
        if (this.startDate == null)
        {
            this.startDate = LocalDate.now();
        }

        if (this.endDate == null)
        {
            this.endDate = this.startDate.plusMonths(1);
        }

        if (this.status == null)
        {
            this.status = SubscriptionStatus.ACTIVE;
        }
    }
}