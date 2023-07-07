package ru.practicum.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ParticipationRequestDto {
    private LocalDateTime created;
    private Long event;
    private Long id;
    private Long requester;
    private String status;
}
