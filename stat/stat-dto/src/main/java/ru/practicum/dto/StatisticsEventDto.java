package ru.practicum.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StatisticsEventDto {
    String app;
    String uri;
    String ip;
    LocalDateTime timestamp;
}
