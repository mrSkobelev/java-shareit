package ru.practicum.shareit.user.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable("userId") long userId) {
        return service.getUserById(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return service.getAllUsers();
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        return service.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable("userId") long userId) {
        return service.updateUser(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") long userId) {
        service.deleteUser(userId);
    }
}