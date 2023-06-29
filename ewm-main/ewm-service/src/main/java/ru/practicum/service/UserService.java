package ru.practicum.service;

import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto add(UserDto user);

    UserDto get(Long id);

    List<UserDto> getAll();

    void delete(Long id);
}
