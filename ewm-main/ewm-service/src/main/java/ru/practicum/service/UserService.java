package ru.practicum.service;

import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto add(UserDto user);

    UserDto get(Long id);

    List<UserDto> getAll(List<Long> ids, Integer from, Integer size);

    void delete(Long id);
}
