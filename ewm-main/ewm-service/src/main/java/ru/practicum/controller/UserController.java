package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.UserDto;
import ru.practicum.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // todo add params
    // GET /admin/users
    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(@RequestParam (defaultValue = "9999") List<Long> ids,
                                                  @RequestParam (defaultValue = "0") Long from,
                                                  @RequestParam (defaultValue = "10") Long size) {
        log.info("Getting users");
        return ResponseEntity.ok().body(userService.getAll());
    }

    // POST /admin/users
    @PostMapping
    public ResponseEntity<UserDto> addUser(@RequestBody UserDto user) {
        log.info("Adding user: {}", user);
        return new ResponseEntity<>(userService.add(user), HttpStatus.CREATED);
    }

    // DELETE /admin/users/{userId}
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        log.info("Deleting user with id: {}", userId);
        userService.delete(userId);
        return new ResponseEntity<>("User deleted", HttpStatus.NO_CONTENT);
    }
}
