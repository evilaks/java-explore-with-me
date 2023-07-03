package ru.practicum.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    private LocalDateTime createdOn;
    private String description;

    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private User initiator;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Location location;

    private Boolean paid;
    private Long participantLimit;
    private LocalDateTime publishedOn;
    private Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    private State state;

    private String title;

}
