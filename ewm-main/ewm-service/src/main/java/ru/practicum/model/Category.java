package ru.practicum.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "categories")
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
