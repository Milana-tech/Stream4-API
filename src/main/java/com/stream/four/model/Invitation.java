package com.stream.four.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "invitations")
@NoArgsConstructor
@AllArgsConstructor
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String inviterUserId;

    private String inviteeEmail;

    private String inviteeUserId;  // set after invitee registers

    private boolean used = false;

    private String token;

    private boolean discountApplied = false;  // set when invitee subscribes

    private LocalDate discountAppliedAt;
}
