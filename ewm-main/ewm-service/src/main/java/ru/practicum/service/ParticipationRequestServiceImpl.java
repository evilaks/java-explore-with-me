package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.request.*;
import ru.practicum.exception.ConflictRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.State;
import ru.practicum.model.User;
import ru.practicum.repo.EventRepo;
import ru.practicum.repo.ParticipationRequestRepo;
import ru.practicum.repo.UserRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final ParticipationRequestRepo partRequestRepo;
    private final ParticipationRequestDtoMapper partRequestDtoMapper;

    private final UserRepo userRepo;
    private final EventRepo eventRepo;

    @Transactional
    @Override
    public ParticipationRequestDto createParticipationRequest(Long userId, Long eventId) {

        // throws 404 if user or event not found
        User requester = userRepo.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found", "User with id " + userId + " not found"));
        Event event = eventRepo.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not found", "Event with id " + eventId + " not found"));

        validateParticipationRequest(requester, event);

        ParticipationRequest request = new ParticipationRequest();
        request.setRequester(requester);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());
        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        ParticipationRequest savedRequest = partRequestRepo.save(request);

        return partRequestDtoMapper.toDto(savedRequest);
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {

        // throws 404 if user or event not found
        User requester = userRepo.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found", "User with id " + userId + " not found"));
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
        User requester = userRepo.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found", "User with id " + userId + " not found"));

        return partRequestRepo.findAllByRequesterId(requester.getId())
                .stream()
                .map(partRequestDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationRequestDto> getParticipationRequestsByEventId(Long userId, Long eventId) {

        User initiator = userRepo.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found", "User with id " + userId + " not found"));
        Event event = eventRepo.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not found", "Event with id " + eventId + " not found"));

        if (!event.getInitiator().getId().equals(initiator.getId())) {
            throw new ConflictRequestException("Request conflict", "User can't get requests for other user's event");
        }

        return partRequestRepo.findAllByEventId(eventId)
                .stream()
                .map(partRequestDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequests(Long userId,
                                                         Long eventId,
                                                         EventRequestsStatusUpdateRequest request) {

        User initiator = userRepo.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found", "User with id " + userId + " not found"));
        Event event = eventRepo.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not found", "Event with id " + eventId + " not found"));

        if (!event.getInitiator().getId().equals(initiator.getId())) {
            throw new ConflictRequestException("Request conflict", "User can't update requests for other user's event");
        }

        // todo maybe refactor this
        request.getRequestIds()
                .forEach(id -> {
                    ParticipationRequest partRequest = partRequestRepo.findById(id).orElseThrow(
                            () -> new NotFoundException("Request not found", "Request with id " + id + " not found")
                    );
                    if (!partRequest.getStatus().equals(RequestStatus.PENDING)) {
                        throw new ConflictRequestException("Bad request", "Wrong request status");
                    }
                    if (event.getParticipantLimit() <= countParticipants(event)) {
                        throw new ConflictRequestException("Request conflict", "Max participants limit reached");
                    }
                    partRequest.setStatus(request.getStatus());
                    partRequestRepo.save(partRequest);
                });

        List<ParticipationRequestDto> confirmedRequests = partRequestRepo.findAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED)
                .stream()
                .map(partRequestDtoMapper::toDto)
                .collect(Collectors.toList());

        List<ParticipationRequestDto> rejectedRequests = partRequestRepo.findAllByEventIdAndStatus(eventId, RequestStatus.REJECTED)
                .stream()
                .map(partRequestDtoMapper::toDto)
                .collect(Collectors.toList());

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(confirmedRequests);
        result.setRejectedRequests(rejectedRequests);

        return result;
    }

    private void validateParticipationRequest(User requester, Event event) {

        if (Objects.equals(event.getInitiator().getId(), requester.getId())) {
            throw new ConflictRequestException("Request conflict", "User can't participate in his own event");
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictRequestException("Request conflict", "User can't participate in unpublished event");
        }

        if (countParticipants(event) >= event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new ConflictRequestException("Request conflict", "Max participants limit reached");
        }

    }

    private Long countParticipants(Event event) {
        return partRequestRepo.countParticipationRequestsByEventAndStatus(event, RequestStatus.CONFIRMED);
    }


}
