package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.EventDtoMapper;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.model.*;
import ru.practicum.repo.EventRepo;
import ru.practicum.repo.LocationRepo;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepo eventRepo;
    private final UserService userService;
    private final CategoryService categoryService;
    private final LocationRepo locationRepo;
    private final EventDtoMapper eventDtoMapper;

    @Override
    @Transactional
    public EventFullDto createEvent(NewEventDto newEventDto, Long userId) {

        User initiator = userService.getUserById(userId); // throws NotFoundException
        Category category = categoryService.getCategoryById(newEventDto.getCategory()); // throws NotFoundException

        Location location = new Location(newEventDto.getLocation().getLat(), newEventDto.getLocation().getLon());
        Location savedLocation = locationRepo.save(location);

        Event event = eventRepo.save(eventDtoMapper.newEventDtoToEntity(
                newEventDto,
                initiator,
                category,
                savedLocation,
                LocalDateTime.now(),
                State.CREATED));

        return eventDtoMapper.toDto(event);
    }
}
