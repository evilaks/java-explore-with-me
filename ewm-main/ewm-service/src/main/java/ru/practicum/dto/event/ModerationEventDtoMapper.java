package ru.practicum.dto.event;

import org.mapstruct.Mapper;
import ru.practicum.model.ModerationEvent;

@Mapper(componentModel = "spring")
public interface ModerationEventDtoMapper {

        ModerationEventDto toDto(ModerationEvent moderationEvent);

        ModerationEvent toEntity(ModerationEventDto moderationEventDto);
}
