package com.stream.four.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Profile")

public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String userId;
    private String name;
    private String avatar;
    private String maturityLevel;   //KIDS, TEENS, ADULTS
    private boolean deleted = false;

}
