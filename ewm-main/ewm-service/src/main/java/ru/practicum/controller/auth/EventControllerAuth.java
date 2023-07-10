package ru.practicum.controller.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventRequest;
import ru.practicum.dto.request.EventRequestsStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.EventService;
import ru.practicum.service.ParticipationRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class EventControllerAuth {

    private final EventService eventService;
    private final ParticipationRequestService participationRequestService;

    // GET /users/{userId}/events?from=0&size=10
    @GetMapping
    public ResponseEntity<List<EventFullDto>> getUserEvents(@PathVariable Long userId,
                                                            @RequestParam(defaultValue = "0") int from,
                                                            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting events from user {} from {} to {}", userId, from, size);
        return new ResponseEntity<>(eventService.getUserEvents(userId, from, size), HttpStatus.OK);
    }

    // POST /users/{userId}/events
    @PostMapping
    public ResponseEntity<EventFullDto> createEvent(@RequestBody NewEventDto event,
                                                    @PathVariable Long userId) {
        log.info("Creating event {} from user {}", event, userId);
        return new ResponseEntity<>(eventService.createEvent(event, userId), HttpStatus.CREATED);
    }

    // GET /users/{userId}/events/{eventId}
    @GetMapping(path = "/{eventId}")
    public ResponseEntity<EventFullDto> getEventByUserAndId(@PathVariable Long userId,
                                                            @PathVariable Long eventId) {
        log.info("Getting event {} from user {}", eventId, userId);
        return new ResponseEntity<>(eventService.getEventByUserAndId(userId, eventId), HttpStatus.OK);
    }

    // PATCH /users/{userId}/events/{eventId}
    @PatchMapping(path = "/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@RequestBody UpdateEventRequest event,
                                                    @PathVariable Long userId,
                                                    @PathVariable Long eventId) {
        log.info("Updating event {} from user {}", eventId, userId);
        return new ResponseEntity<>(eventService.updateEventByUser(event, userId, eventId), HttpStatus.OK);
    }

    // GET /users/{userId}/events/{eventId}/requests
    @GetMapping(path = "/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getEventRequests(@PathVariable Long userId,
                                                                          @PathVariable Long eventId) {
        log.info("Getting requests for event {} from user {}", eventId, userId);
        return new ResponseEntity<>(participationRequestService.getParticipationRequestsByEventId(userId, eventId), HttpStatus.OK);
    }

    // PATCH /users/{userId}/events/{eventId}/requests
    @PatchMapping(path = "/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> acceptRequest(@PathVariable Long userId,
                                                                        @PathVariable Long eventId,
                                                                        @RequestBody EventRequestsStatusUpdateRequest updateRequest) {
        log.info("Updating requests for event {} from user {}", eventId, userId);
        return new ResponseEntity<>(participationRequestService.updateRequests(userId, eventId, updateRequest), HttpStatus.OK);
    }

}
