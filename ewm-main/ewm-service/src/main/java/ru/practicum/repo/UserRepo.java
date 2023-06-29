package ru.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

}
