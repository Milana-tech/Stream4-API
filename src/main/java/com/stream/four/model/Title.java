package com.stream.four.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @ElementCollection(targetClass = VideoQuality.class)
    @CollectionTable(name = "title_supported_qualities", joinColumns = @JoinColumn(name = "title_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "supported_qualities")
    private Set<VideoQuality> supportedQualities = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private MaturityRating maturityRating;

    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "title_content_warnings", joinColumns = @JoinColumn(name = "title_id"))
    @Column(name = "warning")
    private Set<String> contentWarnings = new HashSet<>();
}
