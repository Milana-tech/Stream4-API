package com.stream.four.model.user;

import com.stream.four.model.Auditable;
import com.stream.four.model.enums.Role;
import com.stream.four.model.subscription.Subscription;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class User extends Auditable
{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private int age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    private boolean deleted = false;

    private int failedLoginAttempts = 0;

    private String resetToken;

    public boolean isAccountLocked() {
        return this.failedLoginAttempts >= 3;
    }

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Subscription subscription;

    @Column(name = "created_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdDate = LocalDateTime.now();

    private boolean referralDiscountUsed = false;
    private String invitedBy;
}