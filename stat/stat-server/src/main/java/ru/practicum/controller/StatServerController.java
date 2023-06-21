package ru.practicum.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.StatisticsEventDto;

import java.util.List;


@RestController
public class StatServerController {

    @PostMapping("/hit")
    public ResponseEntity<Object> saveEvent(@RequestBody StatisticsEventDto statEventDto) {
        return null;
    }


    @GetMapping("/stats")
    public ResponseEntity<Object> getStatistics(@RequestParam String start,
                                                @RequestParam String end,
                                                @RequestParam List<String> uris,
                                                @RequestParam (defaultValue = "false") Boolean unique) {
        return null;
    }
}
