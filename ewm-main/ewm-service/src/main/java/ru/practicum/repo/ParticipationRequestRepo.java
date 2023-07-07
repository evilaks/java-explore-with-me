package ru.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.request.RequestStatus;
import ru.practicum.model.ParticipationRequest;

import java.util.List;

@Repository
public interface ParticipationRequestRepo extends JpaRepository<ParticipationRequest, Long> {

    Long countByIdIsAndStatus(Long eventId, RequestStatus status);

    List<ParticipationRequest> findAllByRequesterId(Long requesterId);
}
