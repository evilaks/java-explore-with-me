package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDtoToSave;
import ru.practicum.dto.compilation.CompilationDtoToShow;
import ru.practicum.service.CompilationService;

@Slf4j
@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
public class CompilationControllerAdmin {

    private final CompilationService compilationService;

    // POST /admin/compilations
    @PostMapping
    public ResponseEntity<CompilationDtoToShow> createCompilation(@RequestBody CompilationDtoToSave compilationDto) {
        log.info("Create compilation {}", compilationDto);
        return new ResponseEntity<>(compilationService.createCompilation(compilationDto), HttpStatus.CREATED);
    }

    // DELETE /admin/compilations/{compilationId}
    @DeleteMapping(path = "/{compilationId}")
    public ResponseEntity<Void> deleteCompilation(@PathVariable Long compilationId) {
        log.info("Delete compilation {}", compilationId);
        compilationService.deleteCompilation(compilationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // PATCH /admin/compilations/{compilationId}
    @PatchMapping(path = "/{compilationId}")
    public ResponseEntity<CompilationDtoToShow> updateCompilation(@PathVariable Long compilationId,
                                                                  @RequestBody CompilationDtoToSave compilationDto) {
        log.info("Update compilation {}", compilationDto);
        return new ResponseEntity<>(compilationService.updateCompilation(compilationId, compilationDto), HttpStatus.OK);
    }
}
