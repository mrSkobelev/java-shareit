package ru.practicum.shareit.user.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.EmptyFieldsException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

@Repository
@Data
public class InMemoryUserStorage implements UserStorage {
    private long generatorId = 1;
    private final Map<Long, User> users = new HashMap<>();
    private final List<String> emails = new ArrayList<>();

    @Override
    public UserDto getUserById(long id) {
        if (users.containsKey(id)) {
            User user = users.get(id);
            return UserMapper.toUserDto(user);
        }

        throw new DataNotFoundException("Не найден пользователь с id: " + id);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> userDtoList = new ArrayList<>();

        for (User u : users.values()) {
            userDtoList.add(UserMapper.toUserDto(u));
        }

        return userDtoList;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto.getName() == null || userDto.getEmail() == null) {
            throw new EmptyFieldsException("Поля имя и почта должны быть заполнены");
        }
        User user = UserMapper.toUser(userDto);

        validExistEmail(user);

        user.setId(generatorId++);
        users.put(user.getId(), user);
        emails.add(user.getEmail());

        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        User newUser = UserMapper.toUser(getUserById(userId));
        User responseUser = UserMapper.toUser(userDto);

        if (responseUser.getEmail() != null && !responseUser.getEmail().equals(newUser.getEmail())) {
            validExistEmail(responseUser);
            emails.remove(newUser.getEmail());
            newUser.setEmail(responseUser.getEmail());
            emails.add(newUser.getEmail());
        }

        if (responseUser.getName() != null) {
            newUser.setName(responseUser.getName());
        }

        users.put(userId, newUser);
        return UserMapper.toUserDto(newUser);
    }

    @Override
    public void deleteUser(long id) {
        if (!(users.containsKey(id))) {
            throw new DataNotFoundException("Не найден пользователь с id " + id);
        }
        User user = users.get(id);

        emails.remove(user.getEmail());
        users.remove(id);
    }

    private void validExistEmail(User user) {
        if (emails.contains(user.getEmail())) {
            throw new ValidationException("Этот email уже зарегистрирован");
        }
    }
}