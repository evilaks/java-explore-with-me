package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.compilation.CompilationDtoMapper;
import ru.practicum.dto.compilation.CompilationDtoToSave;
import ru.practicum.dto.compilation.CompilationDtoToShow;
import ru.practicum.dto.event.EventDtoMapper;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repo.CompilationRepo;
import ru.practicum.repo.EventRepo;
import ru.practicum.util.EventEnhancer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepo compilationRepo;
    private final CompilationDtoMapper compilationDtoMapper;
    private final EventRepo eventRepo;
    private final EventDtoMapper eventDtoMapper;
    private final EventEnhancer eventEnhancer;

    @Override
    public List<CompilationDtoToShow> getCompilations(Boolean pinned, Integer from, Integer size) {

        int page = from > 0 ? from / size : 0;

        if (pinned != null) {
            return compilationRepo.findAllByPinned(pinned, PageRequest.of(page, size))
                    .stream()
                    .map(compilation -> compilationDtoMapper.toDto(compilation,
                            eventEnhancer.addViewsAndConfirmedRequests(compilation.getEvents()
                                            .stream()
                                            .map(eventDtoMapper::toDto)
                                            .collect(Collectors.toList()))
                                    .stream()
                                    .map(eventDtoMapper::fullDtoToShortDto)
                                    .collect(Collectors.toList())))
                    .collect(Collectors.toList());
        } else {
            return compilationRepo.findAll(PageRequest.of(page, size))
                    .stream()
                    .map(compilation -> compilationDtoMapper.toDto(compilation,
                            eventEnhancer.addViewsAndConfirmedRequests(compilation.getEvents()
                                    .stream()
                                    .map(eventDtoMapper::toDto)
                                    .collect(Collectors.toList()))
                            .stream()
                            .map(eventDtoMapper::fullDtoToShortDto)
                            .collect(Collectors.toList())))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public CompilationDtoToShow getById(Long id) {


        return compilationRepo.findById(id)
                .map(compilation -> compilationDtoMapper.toDto(compilation,
                        eventEnhancer.addViewsAndConfirmedRequests(compilation.getEvents()
                                .stream()
                                .map(eventDtoMapper::toDto)
                                .collect(Collectors.toList()))
                                .stream()
                                .map(eventDtoMapper::fullDtoToShortDto)
                                .collect(Collectors.toList())))
                .orElseThrow(() -> new NotFoundException("Compilation not found", "Compilation with id " + id + " not found"));
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

        List<EventShortDto> eventShortDtos = events.stream()
                .map(eventDtoMapper::toShortDto)
                .collect(Collectors.toList());

        Compilation compilation = compilationRepo.save(compilationDtoMapper.toEntity(compilationDto, events));

        return compilationDtoMapper.toDto(compilation, eventShortDtos);
    }

    @Transactional
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
            validateCompilation(compilationDto);
            compilation.setTitle(compilationDto.getTitle());
        }

        compilationRepo.save(compilation);

        return compilationDtoMapper.toDto(compilation,
                eventEnhancer.addViewsAndConfirmedRequests(compilation.getEvents()
                                .stream()
                                .map(eventDtoMapper::toDto)
                                .collect(Collectors.toList()))
                        .stream()
                        .map(eventDtoMapper::fullDtoToShortDto)
                        .collect(Collectors.toList()));

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
