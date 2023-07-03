package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.*;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.*;
import ru.practicum.repo.EventRepo;
import ru.practicum.repo.LocationRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepo eventRepo;
    private final UserService userService;
    private final CategoryService categoryService;
    private final LocationRepo locationRepo;
    private final EventDtoMapper eventDtoMapper;
    private final LocationDtoMapper locationDtoMapper;

    @Override
    @Transactional
    public EventFullDto createEvent(NewEventDto newEventDto, Long userId) {

        validateNewEvent(newEventDto);

        User initiator = userService.getUserById(userId); // throws NotFoundException
        Category category = categoryService.getCategoryById(newEventDto.getCategory()); // throws NotFoundException

        Location location = locationDtoMapper.toEntity(newEventDto.getLocation());
        Location savedLocation = locationRepo.save(location);

        // setting default values
        if (newEventDto.getPaid() == null) {
            newEventDto.setPaid(false);
        }

        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(false);
        }

        if (newEventDto.getParticipantLimit() == null) {
            newEventDto.setParticipantLimit(0L);
        }

        Event event = eventRepo.save(eventDtoMapper.newEventDtoToEntity(
                newEventDto,
                initiator,
                category,
                savedLocation,
                LocalDateTime.now(),
                State.PENDING));

        return eventDtoMapper.toDto(event);
    }

    @Override
    public List<EventFullDto> getUserEvents(Long userId, int from, int size) {

        int page = from > 0 ? from / size : 0;

        List<Event> events = eventRepo.findByInitiatorId(userId, PageRequest.of(page, size));
        return events.stream().map(eventDtoMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByUserAndId(Long userId, Long eventId) {
        return eventDtoMapper.toDto(eventRepo.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Not found", "Event not found")));
    }

    @Override
    public EventFullDto updateEventByUser(UpdateEventRequest event, Long userId, Long eventId) {

        // validate event change request
        if (event.getEventDate() != null && event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Invalid event date", "Event date can't be in the past");
        }

        Event eventToUpdate = eventRepo.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Not found", "Event or user not found"));
        if (eventToUpdate.getState() == State.PENDING || eventToUpdate.getState() == State.CANCELED) {
            // update event

            if (event.getAnnotation() != null) {
                eventToUpdate.setAnnotation(event.getAnnotation());
            }
            if (event.getCategory() != null) {
                eventToUpdate.setCategory(categoryService.getCategoryById(event.getCategory()));
            }
            if (event.getDescription() != null) {
                eventToUpdate.setDescription(event.getDescription());
            }
            if (event.getEventDate() != null) {
                eventToUpdate.setEventDate(event.getEventDate());
            }
            // todo there can be problems with location
            if (event.getLocation() != null) {
                eventToUpdate.setLocation(locationDtoMapper.toEntity(event.getLocation()));
            }
            if (event.getPaid() != null) {
                eventToUpdate.setPaid(event.getPaid());
            }
            if (event.getParticipantLimit() != null) {
                eventToUpdate.setParticipantLimit(event.getParticipantLimit());
            }
            if (event.getRequestModeration() != null) {
                eventToUpdate.setRequestModeration(event.getRequestModeration());
            }
            if (event.getTitle() != null) {
                eventToUpdate.setTitle(event.getTitle());
            }
            if (event.getStateAction() != null) {
                switch (event.getStateAction()) {
                    case CANCEL_REVIEW:
                        eventToUpdate.setState(State.CANCELED);
                        break;
                    case SEND_TO_REVIEW:
                        eventToUpdate.setState(State.PENDING);
                        break;
                    default:
                        throw new BadRequestException("Invalid event state action", "Event state action is invalid");
                }
            }

            return eventDtoMapper.toDto(eventRepo.save(eventToUpdate));

        } else {
            throw new ConflictRequestException("Invalid event state", "Event can't be updated");
        }

    }

    private void validateNewEvent(NewEventDto newEventDto) {
        if (newEventDto.getCategory() == null) {
            throw new BadRequestException("Invalid event", "Category is required");
        }

        if (newEventDto.getLocation() == null) {
            throw new BadRequestException("Invalid event", "Location is required");
        }

        if (newEventDto.getEventDate() == null) {
            throw new BadRequestException("Invalid event", "Event date is required");
        }

        if (newEventDto.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ConflictRequestException("Invalid event", "Event date must be in the future");
        }

        if (newEventDto.getAnnotation() == null
                || newEventDto.getAnnotation().length() < 20
                || newEventDto.getAnnotation().length() > 2000) {
            throw new BadRequestException("Invalid event", "Annotation is required");
        }

        if (newEventDto.getDescription() == null
                || newEventDto.getDescription().length() < 20
                || newEventDto.getDescription().length() > 7000) {
            throw new BadRequestException("Invalid event", "Description is required");
        }

        if (newEventDto.getTitle() == null
                || newEventDto.getTitle().length() < 3
                || newEventDto.getTitle().length() > 120) {
            throw new BadRequestException("Invalid event", "Title is required");
        }
    }
}
