package ru.practicum.service;

import ru.practicum.dto.compilation.CompilationDtoToSave;
import ru.practicum.dto.compilation.CompilationDtoToShow;

import java.util.List;

public interface CompilationService {
    List<CompilationDtoToShow> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDtoToShow getById(Long id);

    CompilationDtoToShow createCompilation(CompilationDtoToSave compilationDto);

    void deleteCompilation(Long compilationId);

    CompilationDtoToShow updateCompilation(Long compilationId, CompilationDtoToSave compilationDto);
}
