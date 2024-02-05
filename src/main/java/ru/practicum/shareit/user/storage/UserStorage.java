package ru.practicum.shareit.user.storage;

import java.util.List;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserStorage {
    UserDto getUserById(long id);
    List<UserDto> getAllUsers();
    UserDto createUser(UserDto userDto);
    UserDto updateUser(UserDto userDto, long userId);
    void deleteUser(long id);
}
