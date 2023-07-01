package ru.practicum.service;

import ru.practicum.dto.user.UserDto;
import ru.practicum.model.User;

import java.util.List;

public interface UserService {
    UserDto add(UserDto user);

    UserDto get(Long id);

    List<UserDto> getAll(List<Long> ids, Integer from, Integer size);

    void delete(Long id);

    User getUserById(Long userId);
}
