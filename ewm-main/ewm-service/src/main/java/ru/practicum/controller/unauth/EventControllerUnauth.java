package ru.practicum.controller.unauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.service.EventService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class EventControllerUnauth {

    private final EventService eventService;

    // GET /events?text=0&categories=0&paid=true&rangeStart=2022&rangeEnd=2097&onlyAvailable=false&sort=EVENT_DATE&from=0&size=1000
    @GetMapping
    public ResponseEntity<List<EventFullDto>> getEvents(@RequestParam(required = false) String text,
                                                        @RequestParam(required = false) List<Long> categories,
                                                        @RequestParam(required = false) Boolean paid,
                                                        @RequestParam(required = false) String rangeStart,
                                                        @RequestParam(defaultValue = "3000-01-01 23:59:59") String rangeEnd,
                                                        @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                        @RequestParam(required = false) String sort,
                                                        @RequestParam(defaultValue = "0") int from,
                                                        @RequestParam(defaultValue = "10") int size) {
        log.info("Getting events with params: text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return new ResponseEntity<>(eventService.getEventsUnath(text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                from,
                size), HttpStatus.OK);
    }

    // GET /events/{eventId}
    @GetMapping(path = "/{eventId}")
    public ResponseEntity<EventFullDto> getEvent(@PathVariable Long eventId) {
        log.info("Getting event with id: {}", eventId);
        return new ResponseEntity<>(eventService.getEventById(eventId), HttpStatus.OK);
    }
}
