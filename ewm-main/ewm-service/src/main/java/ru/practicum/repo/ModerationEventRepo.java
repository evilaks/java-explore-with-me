package ru.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.ModerationEvent;

import java.util.List;

public interface ModerationEventRepo extends JpaRepository<ModerationEvent, Long> {
    List<ModerationEvent> findAllByEventId(Long eventId);
}
