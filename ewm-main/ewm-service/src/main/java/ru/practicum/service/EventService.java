package ru.practicum.service;

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.NewEventDto;

public interface EventService {

    EventFullDto createEvent(NewEventDto newEventDto, Long userId);
}
