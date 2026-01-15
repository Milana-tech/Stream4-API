package com.stream.four.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "seasons")
public class Season {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String titleId; // parent series

    private int seasonNumber;
    
    private boolean deleted = false;
}
