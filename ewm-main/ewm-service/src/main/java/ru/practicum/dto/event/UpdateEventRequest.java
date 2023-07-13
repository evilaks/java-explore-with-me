package ru.practicum.dto.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateEventRequest {
    private String annotation;
    private Long category;
    private String description;
    private LocalDateTime eventDate;
    private LocationDto location;
    private Boolean paid;
    private Long participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;
    private String title;
    private String moderationComment;
}
