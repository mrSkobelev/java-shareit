package ru.practicum.shareit.user.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto getUserById(long id) {
        log.info("Получить пользователя с id = {}", id);

        User user = userRepository.findById(id).orElseThrow(
            () -> new NotFoundException("Не найден пользователь с id:" + id));

        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Получить всех пользователей");
        List<User> users = userRepository.findAll();

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
        User user = UserMapper.toUser(userDto);
        User savedUser = userRepository.save(user);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        log.info("Обновить пользователя с id = {}", userId);

        validUser(userId);

        User user = UserMapper.toUser(getUserById(userId));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        User savedUser = userRepository.save(user);

        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public void deleteUser(long id) {
        log.info("Удалить пользователя с id = {}", id);
        userRepository.deleteById(id);
    }

    private User validUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
            () -> new NotFoundException("Не найден пользователь с id: " + userId));
    }
}