package ru.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Location;

@Repository
public interface LocationRepo extends JpaRepository<Location, Long> {

}
