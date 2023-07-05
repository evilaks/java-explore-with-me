package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.event.*;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.*;
import ru.practicum.repo.EventRepo;
import ru.practicum.repo.LocationRepo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    private final StatsClient statsClient;

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
            newEventDto.setRequestModeration(true);
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
    public List<EventFullDto> getEvents(List<Long> users,
                                        List<State> states,
                                        List<Long> categories,
                                        String rangeStart,
                                        String rangeEnd,
                                        int from,
                                        int size) {

        int page = from > 0 ? from / size : 0;

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            if (rangeStart == null) {
                rangeStart = LocalDateTime.now().format(formatter);
            }

            LocalDateTime start = LocalDateTime.parse(rangeStart, formatter);
            LocalDateTime end = LocalDateTime.parse(rangeEnd, formatter);

            List<Event> events = eventRepo.findAllByParams(users,
                    states,
                    categories,
                    start,
                    end,
                    PageRequest.of(page, size));

            return events.stream().map(eventDtoMapper::toDto).collect(Collectors.toList());

        } catch (DateTimeParseException e) {
            throw new BadRequestException("Invalid date format", "Date format should be yyyy-MM-dd HH:mm:ss");
        }

    }

    @Override
    public List<EventFullDto> getEventsUnauth(String text,
                                              List<Long> categories,
                                              Boolean paid,
                                              String rangeStart,
                                              String rangeEnd,
                                              Boolean onlyAvailable,
                                              String sort,
                                              int from,
                                              int size) {

        int page = from > 0 ? from / size : 0;

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            if (rangeStart == null) {
                rangeStart = LocalDateTime.now().format(formatter);
            }

            LocalDateTime start = LocalDateTime.parse(rangeStart, formatter);
            LocalDateTime end = LocalDateTime.parse(rangeEnd, formatter);

            switch (sort) {
                case "EVENT_DATE":
                    sort = "eventDate";
                    break;
                case "VIEWS":
                    sort = "id";  // todo implement views
                    break;
                default:
                    sort = "id";
            }

            List<Event> events = eventRepo.findAllByParamsUnath(text,
                    categories,
                    paid,
                    start,
                    end,
                    // onlyAvailable,
                    PageRequest.of(page, size, Sort.by(sort).descending()));

            return events.stream().map(eventDtoMapper::toDto).collect(Collectors.toList());

        } catch (DateTimeParseException e) {
            throw new BadRequestException("Invalid date format", "Date format should be yyyy-MM-dd HH:mm:ss");
        }
    }

    @Override
    public EventFullDto getEventById(Long eventId) {

        EventFullDto event = eventRepo.findByIdAndState(eventId, State.PUBLISHED)
                .map(eventDtoMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Not found", "Event not found"));

        return event;
    }

    @Override
    public EventFullDto getEventByUserAndId(Long userId, Long eventId) {
        return eventDtoMapper.toDto(eventRepo.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Not found", "Event not found")));
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(UpdateEventRequest updateEventRequest, Long userId, Long eventId) {

        validateUpdateEvent(updateEventRequest);

        Event eventToUpdate = eventRepo.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Not found", "Event or user not found"));
        if (eventToUpdate.getState() == State.PENDING || eventToUpdate.getState() == State.CANCELED) {
            // update event

            updateEvent(eventToUpdate, updateEventRequest);

            if (updateEventRequest.getStateAction() != null) {
                switch (updateEventRequest.getStateAction()) {
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

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(UpdateEventRequest updateEventRequest, Long eventId) {

        Event eventToUpdate = eventRepo.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Not found", "Event not found"));

        validateUpdateEvent(updateEventRequest);

        updateEvent(eventToUpdate, updateEventRequest);

        if (eventToUpdate.getState() == State.PENDING || eventToUpdate.getState() == State.CANCELED) {

            if (updateEventRequest.getStateAction() != null) {
                switch (updateEventRequest.getStateAction()) {
                    case PUBLISH_EVENT:
                        if (eventToUpdate.getState() == State.PENDING) {
                            eventToUpdate.setState(State.PUBLISHED);
                            eventToUpdate.setPublishedOn(LocalDateTime.now());
                        } else {
                            throw new ConflictRequestException("Invalid event state", "Event can't be updated");
                        }
                        break;
                    case REJECT_EVENT:
                        eventToUpdate.setState(State.CANCELED);
                        break;
                    default:
                        throw new BadRequestException("Invalid event state action", "Event state action is invalid");
                }
            }

            // validate event change request
            if (eventToUpdate.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new BadRequestException("Invalid event date", "Event date can't be in the past");
            }

            return eventDtoMapper.toDto(eventRepo.save(eventToUpdate));

        } else {
            throw new ConflictRequestException("Invalid event state", "Event can't be updated");
        }
    }

    private void validateUpdateEvent(UpdateEventRequest updateEventRequest) {
        if (updateEventRequest.getEventDate() != null && updateEventRequest.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ConflictRequestException("Invalid event", "Event date must be in the future");
        }

        if (updateEventRequest.getAnnotation() != null &&
                (updateEventRequest.getAnnotation().length() < 20
                || updateEventRequest.getAnnotation().length() > 2000)) {
            throw new BadRequestException("Invalid event", "Invalid annotation length");
        }

        if (updateEventRequest.getDescription() != null &&
                (updateEventRequest.getDescription().length() < 20
                || updateEventRequest.getDescription().length() > 7000)) {
            throw new BadRequestException("Invalid event", "Invalid description length");
        }

        if (updateEventRequest.getTitle() != null &&
                (updateEventRequest.getTitle().length() < 3
                || updateEventRequest.getTitle().length() > 120)) {
            throw new BadRequestException("Invalid event", "Invalid title length");
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

    private void updateEvent(Event event, UpdateEventRequest eventRequest) {
        if (eventRequest.getAnnotation() != null) {
            event.setAnnotation(eventRequest.getAnnotation());
        }
        if (eventRequest.getCategory() != null) {
            event.setCategory(categoryService.getCategoryById(eventRequest.getCategory()));
        }
        if (eventRequest.getDescription() != null) {
            event.setDescription(eventRequest.getDescription());
        }
        if (eventRequest.getEventDate() != null) {
            event.setEventDate(eventRequest.getEventDate());
        }
        // todo there can be problems with location
        if (eventRequest.getLocation() != null) {
            event.setLocation(locationDtoMapper.toEntity(eventRequest.getLocation()));
        }
        if (eventRequest.getPaid() != null) {
            event.setPaid(eventRequest.getPaid());
        }
        if (eventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(eventRequest.getParticipantLimit());
        }
        if (eventRequest.getRequestModeration() != null) {
            event.setRequestModeration(eventRequest.getRequestModeration());
        }
        if (eventRequest.getTitle() != null) {
            event.setTitle(eventRequest.getTitle());
        }
    }
}
