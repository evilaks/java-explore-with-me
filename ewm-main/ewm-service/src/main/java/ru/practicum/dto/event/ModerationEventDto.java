package ru.practicum.dto.event;

import lombok.Data;
import ru.practicum.model.State;

import java.time.LocalDateTime;

@Data
public class ModerationEventDto {
    private LocalDateTime timestamp;
    private State previousState;
    private State newState;
    private String comment;
}
