package ru.practicum.dto.user;

import org.mapstruct.Mapper;
import ru.practicum.model.User;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userDto);

    UserShortDto toShortDto(User user);
}
