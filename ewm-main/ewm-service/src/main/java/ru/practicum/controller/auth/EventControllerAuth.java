package ru.practicum.controller.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.service.EventService;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class EventControllerAuth {

    private final EventService eventService;

    // GET /users/{userId}/events

    // POST /users/{userId}/events
    @PostMapping
    public ResponseEntity<EventFullDto> createEvent(@RequestBody NewEventDto event,
                                                    @PathVariable Long userId) {
        log.info("Creating event {} from user {}", event, userId);
        return ResponseEntity.ok().body(eventService.createEvent(event, userId));
    }

    // GET /users/{userId}/events/{eventId}

    // PATCH /users/{userId}/events/{eventId}

    // GET /users/{userId}/events/{eventId}/requests

    // PATCH /users/{userId}/events/{eventId}/requests


}
