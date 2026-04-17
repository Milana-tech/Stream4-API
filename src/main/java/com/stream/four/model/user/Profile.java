package com.stream.four.model.user;

import com.stream.four.model.enums.ContentWarning;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

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
    private int age;
    private String maturityLevel;   //KIDS, TEENS, ADULT
    @ElementCollection(targetClass = ContentWarning.class)
    @CollectionTable(name = "profile_filters", joinColumns = @JoinColumn(name = "profile_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "indicator")
    private List<ContentWarning> contentFilters;
    private boolean deleted = false;

}
