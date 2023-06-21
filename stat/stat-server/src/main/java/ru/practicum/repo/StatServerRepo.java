package ru.practicum.repo;

import ru.practicum.model.StatEvent;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StatServerRepo extends JpaRepository<StatEvent, Long> {
}
