package ru.practicum.service;

import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    ParticipationRequestDto createParticipationRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getParticipationRequestsByUserId(Long userId);

    List<ParticipationRequestDto> getParticipationRequestsByEventId(Long eventId);
}
