package ru.practicum.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Event;
import ru.practicum.model.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepo extends JpaRepository<Event, Long> {
    List<Event> findByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long userId, Long eventId);

    @Query("SELECT e FROM Event e WHERE (e.initiator.id IN :users or :users is null) " +
            "AND (e.state IN :states or :states is null)" +
            "AND (e.category.id IN :categories or :categories is null)" +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "ORDER BY e.eventDate DESC")
    List<Event> findAllByParams(@Param("users")List<Long> users,
                                @Param("states") List<State> states,
                                @Param("categories") List<Long> categories,
                                LocalDateTime rangeStart,
                                LocalDateTime rangeEnd,
                                Pageable pageable);

    @Query("SELECT e FROM Event e WHERE (e.description LIKE :text or e.annotation LIKE :text or :text is null) " +
            "AND (e.state = 'PUBLISHED')" +
            "AND (e.category.id IN :categories or :categories is null)" +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "AND (e.paid = :paid or :paid is null)" +
            "ORDER BY e.eventDate DESC")
    List<Event> findAllByParamsUnath(String text,
                                     List<Long> categories,
                                     Boolean paid,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     Pageable pageable);

    Optional<Event> findByIdAndState(Long eventId, State state);

    List<Event> findAllByStateAndEventDateBetween(State state, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
