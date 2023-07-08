package ru.practicum.service;

import ru.practicum.dto.request.EventRequestsStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    ParticipationRequestDto createParticipationRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getParticipationRequestsByUserId(Long userId);

    List<ParticipationRequestDto> getParticipationRequestsByEventId(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequests(Long userId,
                                                        Long eventId,
                                                        EventRequestsStatusUpdateRequest request);
}
