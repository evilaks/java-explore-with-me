package ru.practicum.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "moderation_events")
@Entity
public class ModerationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;

    @Enumerated(EnumType.STRING)
    private State previousState;

    @Enumerated(EnumType.STRING)
    private State newState;

    private String comment;

}
