package ru.practicum.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.model.StatEvent;

@Mapper(componentModel = "spring")
public interface StatEventDtoMapper {

    @Mapping(target = "id", source = "id")
    StatisticsEventDto toDto(StatEvent statEvent);

    StatEvent toEntity(StatisticsEventDto statisticsEventDto);
}
