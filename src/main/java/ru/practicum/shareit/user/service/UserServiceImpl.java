package ru.practicum.shareit.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.EmptyFieldsException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository storage;

    @Override
    public UserDto getUserById(long id) {
        log.info("Получить пользователя с id = {}", id);

        Optional<User> optionalUser = storage.findById(id);

        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException("Не найден пользователь с id: " + id);
        }

        return UserMapper.toUserDto(optionalUser.get());
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Получить всех пользователей");
        List<User> users = storage.findAll();

        List<UserDto> userDtoList = new ArrayList<>();
        if (!users.isEmpty()) {
            for (User u : users) {
                UserDto userDto = UserMapper.toUserDto(u);
                userDtoList.add(userDto);
            }
        }
        return userDtoList;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Создать пользователя " + userDto.getEmail());
        validCreation(userDto);
        User user = UserMapper.toUser(userDto);
        User savedUser = storage.save(user);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        log.info("Обновить пользователя с id = {}", userId);

        User user = UserMapper.toUser(getUserById(userId));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        User savedUser = storage.save(user);

        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public void deleteUser(long id) {
        log.info("Удалить пользователя с id = {}", id);
        storage.deleteById(id);
    }

    private void validCreation(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getName() == null ||
            userDto.getEmail().isBlank() || userDto.getName().isBlank()) {
            throw new EmptyFieldsException("Не все поля заполнены для создания пользователя");
        }
    }
}