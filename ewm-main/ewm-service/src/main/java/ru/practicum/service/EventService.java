package ru.practicum.service;

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventRequest;
import ru.practicum.model.State;

import java.util.List;

public interface EventService {

    EventFullDto createEvent(NewEventDto newEventDto, Long userId);

    List<EventFullDto> getUserEvents(Long userId, int from, int size);

    List<EventFullDto> getEvents(List<Long> users,
                                 List<State> states,
                                 List<Long> categories,
                                 String rangeStart,
                                 String rangeEnd,
                                 int from,
                                 int size);

    List<EventFullDto> getEventsUnauth(String text,
                                       List<Long> categories,
                                       Boolean paid,
                                       String rangeStart,
                                       String rangeEnd,
                                       Boolean onlyAvailable,
                                       String sort,
                                       int from,
                                       int size);

    EventFullDto getEventDtoById(Long eventId);

    EventFullDto getEventByUserAndId(Long userId, Long eventId);

    EventFullDto updateEventByUser(UpdateEventRequest event, Long userId, Long eventId);

    EventFullDto updateEventByAdmin(UpdateEventRequest event, Long eventId);

}
