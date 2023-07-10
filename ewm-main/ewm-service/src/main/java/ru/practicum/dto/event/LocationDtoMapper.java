package ru.practicum.dto.event;

import org.mapstruct.Mapper;
import ru.practicum.model.Location;

@Mapper(componentModel = "spring")
public interface LocationDtoMapper {

    LocationDto toDto(Location location);

    Location toEntity(LocationDto locationDto);
}
