package ru.practicum.dto.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.event.EventDtoMapper;
import ru.practicum.dto.user.UserDtoMapper;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.User;

@Mapper(componentModel = "spring", uses = {UserDtoMapper.class, EventDtoMapper.class})
public interface ParticipationRequestDtoMapper {

    @Mapping(target = "requester", source = "requester.id")
    @Mapping(target = "event", source = "event.id")
    ParticipationRequestDto toDto(ParticipationRequest request);


    @Mapping(target = "id", source = "requestDto.id")
    @Mapping(target = "requester", source = "requester")
    @Mapping(target = "event", source = "event")
    ParticipationRequest toEntity(ParticipationRequestDto requestDto, User requester, Event event);
}
