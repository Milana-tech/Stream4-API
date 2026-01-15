package com.stream.four.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "watch_events")
public class WatchEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String userId;

    private String titleId;

    private int progressSeconds; // how far the user watched

    private boolean finished = false;

    private long lastUpdated; // timestamp
}
