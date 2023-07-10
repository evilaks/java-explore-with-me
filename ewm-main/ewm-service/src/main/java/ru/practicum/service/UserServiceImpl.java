package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserDtoMapper;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.repo.UserRepo;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final UserDtoMapper userDtoMapper;

    @Override
    public UserDto add(UserDto user) {
        validateUser(user);
        return userDtoMapper.toDto(userRepo.save(userDtoMapper.toEntity(user)));
    }

    @Override
    public UserDto get(Long id) {
        return userDtoMapper.toDto(userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found", "User with id " + id + " not found")));
    }

    @Override
    public List<UserDto> getAll(List<Long> ids, Integer from, Integer size) {

        int page = from > 0 ? from / size : 0;

        if (ids == null) {
            return userRepo.findAll(PageRequest.of(page, size)).stream()
                    .map(userDtoMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            return userRepo.findAllByIdIn(ids, PageRequest.of(page, size)).stream()
                    .map(userDtoMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void delete(Long id) {

        if (!userRepo.existsById(id))
            throw new NotFoundException("User not found", "User with id " + id + " not found");

        userRepo.deleteById(id);
    }

    private void validateUser(UserDto user) {
        if (user == null)
            throw new BadRequestException("Invalid user", "User is null");

        if (user.getName() == null || user.getName().isBlank())
            throw new BadRequestException("Invalid user", "User name is empty");

        if (user.getName().length() < 2)
            throw new BadRequestException("Invalid user", "User name is too short");

        if (user.getName().length() > 250)
            throw new BadRequestException("Invalid user", "User name is too long");

        String nameRegex = "^[a-zA-Z0-9][a-zA-Z0-9. ']*$";
        Pattern namePattern = Pattern.compile(nameRegex);
        if (!namePattern.matcher(user.getName()).matches())
            throw new BadRequestException("Invalid user", "User name is invalid");

        if (user.getEmail() == null || user.getEmail().isEmpty())
            throw new BadRequestException("Invalid user", "User email is empty");

        if (user.getEmail().length() > 254)
            throw new BadRequestException("Invalid user", "User email is too long");

        String emailRegex = "^[a-zA-Z0-9_.+&*-]{1,64}@(?:[a-zA-Z0-9-]{1,63}+\\.)+([a-zA-Z0-9.]{2,127})$";

        Pattern emailPattern = Pattern.compile(emailRegex);
        if (!emailPattern.matcher(user.getEmail()).matches())
            throw new BadRequestException("Invalid user", "User email is invalid");
    }
}
