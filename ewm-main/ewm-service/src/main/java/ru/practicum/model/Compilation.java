package ru.practicum.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Table(name = "compilations")
@Entity
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Boolean pinned;

    @ManyToMany
    @JoinTable(
        name = "compilations_events",
        joinColumns = @JoinColumn(name = "compilation_id"),
        inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private List<Event> events;

}
