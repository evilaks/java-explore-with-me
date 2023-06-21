package ru.practicum.model;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StatEvent {
    Long id;
    String app;
    String uri;
    String ip;
    LocalDateTime timestamp;
}