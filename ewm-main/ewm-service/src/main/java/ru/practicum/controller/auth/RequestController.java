package ru.practicum.controller.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.ParticipationRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
public class RequestController {

    private final ParticipationRequestService participationRequestService;

    // GET /users/{userId}/requests
    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getRequests(@PathVariable Long userId) {
        log.info("Getting requests for user {}", userId);
        return new ResponseEntity<>(participationRequestService.getParticipationRequestsByUserId(userId), HttpStatus.OK);
    }

    // POST /users/{userId}/requests
    @PostMapping
    public ResponseEntity<ParticipationRequestDto> createRequest(@PathVariable Long userId,
                                                                 @RequestParam Long eventId) {
        log.info("Creating request from user {}", userId);
        return new ResponseEntity<>(participationRequestService.createParticipationRequest(userId, eventId), HttpStatus.CREATED);
    }

    // PATCH /users/{userId}/requests/{requestId}/cancel
    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(@PathVariable Long userId,
                                                                 @PathVariable Long requestId) {
        log.info("Cancelling request {} from user {}", requestId, userId);
        return new ResponseEntity<>(participationRequestService.cancelParticipationRequest(userId, requestId), HttpStatus.OK);
    }

}
