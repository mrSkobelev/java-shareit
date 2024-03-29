package ru.practicum.shareit.user.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable("userId") long userId) {
        log.info("Get user by id {}", userId);
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Get all users");
        return userClient.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Create user {}", userDto);
        if (userDto.getEmail() == null) {
            throw new NullPointerException("Поле email не должно быть пустым при создании пользователя");
        }
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserDto userDto, @PathVariable("userId") long userId) {
        log.info("Update user {} by userId {}", userDto, userId);
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable("userId") long userId) {
        log.info("Delete user by id {}", userId);
        return userClient.deleteUser(userId);
    }
}