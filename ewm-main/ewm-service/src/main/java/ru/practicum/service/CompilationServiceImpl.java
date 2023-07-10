package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.compilation.CompilationDtoMapper;
import ru.practicum.dto.compilation.CompilationDtoToSave;
import ru.practicum.dto.compilation.CompilationDtoToShow;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repo.CompilationRepo;
import ru.practicum.repo.EventRepo;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepo compilationRepo;
    private final CompilationDtoMapper compilationDtoMapper;
    private final EventRepo eventRepo;

    @Override
    public List<CompilationDtoToShow> getAll() {
        return null;
    }

    @Override
    public CompilationDtoToShow getById(Long id) {
        return null;
    }

    @Transactional
    @Override
    public CompilationDtoToShow createCompilation(CompilationDtoToSave compilationDto) {

        validateCompilation(compilationDto);

        if (compilationDto.getPinned() == null) {
            compilationDto.setPinned(false);
        }

        List<Event> events;
        if (compilationDto.getEvents() != null) {
            events = eventRepo.findAllById(compilationDto.getEvents());
        } else {
            events = new ArrayList<>();
        }

        Compilation compilation = compilationRepo.save(compilationDtoMapper.toEntity(compilationDto, events));

        return compilationDtoMapper.toDto(compilation, events);
    }

    @Override
    public void deleteCompilation(Long compilationId) {
        if (compilationRepo.existsById(compilationId)) {
            compilationRepo.deleteById(compilationId);
        } else {
            throw new NotFoundException("Not found", "Compilation with id " + compilationId + " not found");
        }
    }

    @Transactional
    @Override
    public CompilationDtoToShow updateCompilation(Long compilationId, CompilationDtoToSave compilationDto) {

        validateCompilation(compilationDto);

        List<Event> events;
        Compilation compilation = compilationRepo.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Not found", "Compilation with id " + compilationId + " not found"));

        if (compilationDto.getEvents() != null) {
            if (compilationDto.getEvents().isEmpty()) {
                events = new ArrayList<>();
            } else {
                events = eventRepo.findAllById(compilationDto.getEvents());
            }
            compilation.setEvents(events);
        }

        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }

        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }

        compilationRepo.save(compilation);

        return compilationDtoMapper.toDto(compilation, compilation.getEvents());

    }

    private void validateCompilation(CompilationDtoToSave compilationDto) {
        if (compilationDto.getTitle() == null || compilationDto.getTitle().isBlank()) {
            throw new BadRequestException("Invalid compilation", "Compilation title must not be empty");
        }

        if (compilationDto.getTitle().length() > 50 || compilationDto.getTitle().length() < 3) {
            throw new BadRequestException("Invalid compilation", "Compilation title must be between 3 and 50 characters");
        }
    }
}
