package ru.practicum.shareit.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void getUserById_whenUserCreated_thenReturnUser() {
        long id = 0;

        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserDto resultUserDto = userService.getUserById(id);
        User resultUser = UserMapper.toUser(resultUserDto);

        assertEquals(user.getName(), resultUser.getName());
    }

    @Test
    void getUserById_whenUserNotFound_thenReturnTrowNotFoundException() {
        long userId = 0;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void getAllUsers_whenUsersCreated_thenReturnListAndEqualsSize() {
        List<User> savedUsers = List.of(new User(), new User());

        when(userRepository.findAll()).thenReturn(savedUsers);

        List<UserDto> actualUsers = userService.getAllUsers();

        assertEquals(savedUsers.size(), actualUsers.size());
        verify(userRepository).findAll();
    }

    @Test
    void createUser_whenUserValid_thenReturnUser() {
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("email@mail.com");

        UserDto userDto = UserMapper.toUserDto(user);

        when(userRepository.save(user)).thenReturn(user);

        UserDto resultUserDto = userService.createUser(userDto);
        User resultUser = UserMapper.toUser(resultUserDto);

        assertEquals(user, resultUser);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_whenUserValid_thenReturnNewUser() {
        long userId = 1L;

        User oldUser = new User();
        oldUser.setId(1L);
        oldUser.setName("name");
        oldUser.setEmail("name@mail.ru");

        when(userRepository.findById(userId))
            .thenReturn(Optional.of(oldUser));

        UserDto newUserDto = new UserDto();
        newUserDto.setName("newName");
        newUserDto.setEmail("newName@mail.ru");
        User newUser = UserMapper.toUser(newUserDto);
        newUser.setId(1L);

        when(userRepository.save(newUser)).thenReturn(newUser);

        UserDto actualUserDto = userService.updateUser(newUserDto, userId);

        assertEquals("newName", actualUserDto.getName());
        assertEquals("newName@mail.ru", actualUserDto.getEmail());
        verify(userRepository).save(newUser);
    }

    @Test
    void deleteUser_whenUserCreated_thenVerify() {
        long userId = 0L;

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }
}