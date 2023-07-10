package ru.practicum.controller.unauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDtoToShow;
import ru.practicum.service.CompilationService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
public class CompilationControllerUnauth {

    private final CompilationService compilationService;

    // GET /compilations
    @GetMapping
    public ResponseEntity<List<CompilationDtoToShow>> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                                      @RequestParam(defaultValue = "0") Integer from,
                                                                      @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get compilations with params pinned={}, from={} and size={}", pinned, from, size);
        return new ResponseEntity<>(compilationService.getCompilations(pinned, from, size), HttpStatus.OK);
    }

    // GET /compilations/{compilationId}
    @GetMapping(path = "/{compilationId}")
    public ResponseEntity<CompilationDtoToShow> getCompilationById(@PathVariable Long compilationId) {
        log.info("Get compilation by id {}", compilationId);
        return new ResponseEntity<>(compilationService.getById(compilationId), HttpStatus.OK);
    }
}
