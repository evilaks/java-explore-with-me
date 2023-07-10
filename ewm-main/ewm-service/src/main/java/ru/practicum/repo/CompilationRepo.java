package ru.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Compilation;

@Repository
public interface CompilationRepo extends JpaRepository<Compilation, Long> {

}
