package ru.practicum.shareit.user.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage storage;

    @Override
    public UserDto getUserById(long id) {
        log.info("Получить пользователя с id = {}", id);
        return storage.getUserById(id);
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Получить всех пользователей");
        return storage.getAllUsers();
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Создать пользователя");
        return storage.createUser(userDto);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        log.info("Обновить пользователя с id = {}", userDto);
        return storage.updateUser(userDto, userId);
    }

    @Override
    public void deleteUser(long id) {
        log.info("Удалить пользователя с id = {}", id);
        storage.deleteUser(id);
    }
}