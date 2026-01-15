package com.stream.four.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "titles")
public class Title {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    @Column(length = 2000)
    private String description;

    private int releaseYear;

    @Enumerated(EnumType.STRING)
    private TitleType type; // MOVIE or SERIES 
    
    private String genre; 
    
    private boolean deleted = false;
}
