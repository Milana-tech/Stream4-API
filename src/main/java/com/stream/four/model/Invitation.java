package com.stream.four.model;

import jakarta.persistence.*;
import lombok.*;

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

    private boolean used = false;

    private String token;

}
