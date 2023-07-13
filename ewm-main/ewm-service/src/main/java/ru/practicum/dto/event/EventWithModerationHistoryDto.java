package ru.practicum.dto.event;

import lombok.Data;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.model.State;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventWithModerationHistoryDto {
    private Long id;
    private String annotation;
    private CategoryDto category;

    private LocalDateTime createdOn;
    private String description;
    private LocalDateTime eventDate;

    private UserShortDto initiator;

    private LocationDto location;

    private Boolean paid;
    private Long participantLimit;
    private LocalDateTime publishedOn;
    private Boolean requestModeration;

    private State state;

    private String title;
    private List<ModerationEventDto> moderationHistory;
}
