package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventRequest;
import ru.practicum.model.State;
import ru.practicum.service.EventService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class EventControllerAdmin {

    private final EventService eventService;

    // GET /admin/events?users=1,2&states=PENDING,CANCELED&categories=1,2&rangeStart=date&rangeEnd=date&from=0&size=10
    @GetMapping
    public ResponseEntity<List<EventFullDto>> getEvents(@RequestParam(required = false) List<Long> users,
                                                        @RequestParam(required = false) List<State> states,
                                                        @RequestParam(required = false) List<Long> categories,
                                                        @RequestParam(defaultValue = "2000-01-01 00:00:01") String rangeStart,
                                                        @RequestParam(defaultValue = "2100-01-01 00:00:01") String rangeEnd,
                                                        @RequestParam(defaultValue = "0") int from,
                                                        @RequestParam(defaultValue = "10") int size) {
        log.info("Getting events with params users={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return new ResponseEntity<>(eventService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size), HttpStatus.OK);
    }

    // PATCH /admin/events/{eventId}
    @PatchMapping(path = "/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@PathVariable Long eventId,
                                                    @RequestBody UpdateEventRequest event) {
        log.info("Updating event {} with data {}", eventId, event);
        return new ResponseEntity<>(eventService.updateEventByAdmin(event, eventId), HttpStatus.OK);
    }

}
