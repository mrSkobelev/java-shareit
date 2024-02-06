package ru.practicum.shareit.user.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage storage;

    @Override
    public UserDto getUserById(long id) {
        return storage.getUserById(id);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return storage.getAllUsers();
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        return storage.createUser(userDto);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        return storage.updateUser(userDto, userId);
    }

    @Override
    public void deleteUser(long id) {
        storage.deleteUser(id);
    }
}