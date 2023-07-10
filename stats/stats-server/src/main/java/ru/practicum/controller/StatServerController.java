package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.StatisticsEventDto;
import ru.practicum.dto.StatisticsReportDto;
import ru.practicum.service.StatServerServiceImpl;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatServerController {

    private final StatServerServiceImpl service;

    @PostMapping("/hit")
    public ResponseEntity<StatisticsEventDto> saveEvent(@RequestBody StatisticsEventDto statEventDto) {
        log.info("Saving event: {}", statEventDto);
        return new ResponseEntity<>(service.save(statEventDto), HttpStatus.CREATED);
    }


    @GetMapping("/stats")
    public ResponseEntity<List<StatisticsReportDto>> getStatistics(@RequestParam String start,
                                                                   @RequestParam String end,
                                                                   @RequestParam (defaultValue = "ALL") List<String> uris,
                                                                   @RequestParam (defaultValue = "false") Boolean unique) {
        log.info("Getting statistics for period {} - {}, uris: {}, unique: {}", start, end, uris, unique);
        return ResponseEntity.ok().body(service.getStatistics(start, end, uris, unique));
    }
}
