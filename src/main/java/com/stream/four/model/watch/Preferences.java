package com.stream.four.model.watch;

import com.stream.four.model.enums.Genre;
import com.stream.four.model.enums.MaturityRating;
import com.stream.four.model.enums.TitleType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "preferences")
public class Preferences {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String profileId;

    @ElementCollection(targetClass = Genre.class)
    @CollectionTable(name = "preferences_genres", joinColumns = @JoinColumn(name = "preferences_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "genre")
    private Set<Genre> preferredGenres;

    @Enumerated(EnumType.STRING)
    private TitleType preferredType; // null means no preference (both movies and series)

    @Enumerated(EnumType.STRING)
    private MaturityRating minimumMaturityRating; // null means no minimum
}
