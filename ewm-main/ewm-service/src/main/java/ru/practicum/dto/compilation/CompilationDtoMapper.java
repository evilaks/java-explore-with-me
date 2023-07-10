package ru.practicum.dto.compilation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.event.EventDtoMapper;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.List;

@Mapper(componentModel = "spring", uses = EventDtoMapper.class)
public interface CompilationDtoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", source = "events")
    Compilation toEntity(CompilationDtoToSave compilationDto, List<Event> events);

    @Mapping(target = "events", source = "events")
    CompilationDtoToShow toDto(Compilation compilation, List<EventShortDto> events);

}
