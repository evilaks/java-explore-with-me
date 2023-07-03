package ru.practicum.service;

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventRequest;

import java.util.List;

public interface EventService {

    EventFullDto createEvent(NewEventDto newEventDto, Long userId);

    List<EventFullDto> getUserEvents(Long userId, int from, int size);

    EventFullDto getEventByUserAndId(Long userId, Long eventId);

    EventFullDto updateEventByUser(UpdateEventRequest event, Long userId, Long eventId);
}
