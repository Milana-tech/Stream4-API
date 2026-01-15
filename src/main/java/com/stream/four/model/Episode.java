package com.stream.four.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "episodes")
public class Episode {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String seasonId;

    private int episodeNumber;

    private String name;

    @Column(length = 2000)
    private String description;
    
    private boolean deleted = false;
}
