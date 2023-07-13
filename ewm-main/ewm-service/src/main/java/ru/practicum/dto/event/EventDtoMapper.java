package ru.practicum.dto.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.category.CategoryDtoMapper;
import ru.practicum.dto.user.UserDtoMapper;
import ru.practicum.model.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UserDtoMapper.class, CategoryDtoMapper.class, ModerationEventDtoMapper.class})
public interface EventDtoMapper {

        EventFullDto toDto(Event event);

        Event toEntity(EventFullDto eventDto);

        EventShortDto toShortDto(Event event);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "category", source = "category")
        @Mapping(target = "publishedOn", ignore = true)
        Event newEventDtoToEntity(NewEventDto newEventDto,
                                  User initiator,
                                  Category category,
                                  Location location,
                                  LocalDateTime createdOn,
                                  State state);

        EventShortDto fullDtoToShortDto(EventFullDto event);

        @Mapping(target = "moderationHistory", source = "moderationEvents")
        EventWithModerationHistoryDto toEventWithModerationHistoryDto(Event event,
                                                                      List<ModerationEvent> moderationEvents);

}
