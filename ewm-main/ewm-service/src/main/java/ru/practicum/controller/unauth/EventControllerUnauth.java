package ru.practicum.controller.unauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.StatisticsEventDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class EventControllerUnauth {

    private final EventService eventService;
    private final StatsClient statsClient;

    // GET /events?text=0&categories=0&paid=true&rangeStart=2022&rangeEnd=2097&onlyAvailable=false&sort=EVENT_DATE&from=0&size=1000
    @GetMapping
    public ResponseEntity<List<EventFullDto>> getEvents(@RequestParam(required = false) String text,
                                                        @RequestParam(required = false) List<Long> categories,
                                                        @RequestParam(required = false) Boolean paid,
                                                        @RequestParam(required = false) String rangeStart,
                                                        @RequestParam(defaultValue = "3000-01-01 23:59:59") String rangeEnd,
                                                        @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                        @RequestParam(defaultValue = "ID") String sort,
                                                        @RequestParam(defaultValue = "0") int from,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        HttpServletRequest request) {

        log.info("Getting events with params: text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        statsClient.saveEvent(StatisticsEventDto.builder()
                        .app("ewm-service")
                        .ip(request.getRemoteAddr())
                        .uri(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build());

        return new ResponseEntity<>(eventService.getEventsUnauth(text,
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
    public ResponseEntity<EventFullDto> getEvent(@PathVariable Long eventId,
                                                 HttpServletRequest request) {
        log.info("Getting event with id: {}", eventId);

        statsClient.saveEvent(StatisticsEventDto.builder()
                .app("ewm-service")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build());

        return new ResponseEntity<>(eventService.getEventDtoById(eventId), HttpStatus.OK);
    }
}
