package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.request.ParticipationRequestDtoMapper;
import ru.practicum.dto.request.RequestStatus;
import ru.practicum.exception.ConflictRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.State;
import ru.practicum.model.User;
import ru.practicum.repo.ParticipationRequestRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final ParticipationRequestRepo partRequestRepo;
    private final ParticipationRequestDtoMapper partRequestDtoMapper;

    private final UserService userService;
    private final EventService eventService;

    @Override
    public ParticipationRequestDto createParticipationRequest(Long userId, Long eventId) {

        // throws 404 if user or event not found
        User requester = userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);

        validateParticipationRequest(requester, event);

        ParticipationRequest request = new ParticipationRequest();
        request.setRequester(requester);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());
        if (event.getRequestModeration()) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.APPROVED);
        }

        ParticipationRequest savedRequest = partRequestRepo.save(request);

        return partRequestDtoMapper.toDto(savedRequest);
    }

    @Override
    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {

        // throws 404 if user or event not found
        User requester = userService.getUserById(userId);
        ParticipationRequest request = partRequestRepo.findById(requestId).orElseThrow(
                () -> new NotFoundException("Request not found", "Request with id " + requestId + " not found")
        );

        if (request.getRequester().getId().equals(requester.getId())) {
            request.setStatus(RequestStatus.CANCELED);
            ParticipationRequest savedRequest = partRequestRepo.save(request);
            return partRequestDtoMapper.toDto(savedRequest);
        } else {
            throw new ConflictRequestException("Request conflict", "User can't cancel other user's request");
        }
    }

    @Override
    public List<ParticipationRequestDto> getParticipationRequestsByUserId(Long userId) {
        // throws 404 if user or event not found
        User requester = userService.getUserById(userId);

        return partRequestRepo.findAllByRequesterId(requester.getId())
                .stream()
                .map(partRequestDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationRequestDto> getParticipationRequestsByEventId(Long eventId) {
        return null;
    }

    private void validateParticipationRequest(User requester, Event event) {

        if (Objects.equals(event.getInitiator().getId(), requester.getId())) {
            throw new ConflictRequestException("Request conflict", "User can't participate in his own event");
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictRequestException("Request conflict", "User can't participate in unpublished event");
        }

        if (countParticipants(event) > event.getParticipantLimit()) {
            throw new ConflictRequestException("Request conflict", "Max participants limit reached");
        }


    }

    private Long countParticipants(Event event) {
        return partRequestRepo.countByIdIsAndStatus(event.getId(), RequestStatus.APPROVED);
    }


}
