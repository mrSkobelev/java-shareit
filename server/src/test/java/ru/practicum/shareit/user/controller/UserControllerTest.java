package ru.practicum.shareit.user.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Test
    void getUserById_whenUserFound_thenReturnedUser() {
        long userId = 1L;
        UserDto userDto = new UserDto();

        when(userService.getUserById(userId)).thenReturn(userDto);

        UserDto result = userController.getUserById(userId);

        assertEquals(result, userDto);
    }

    @Test
    void getUserById_whenUserNotFound_thenReturnThrow() {
        long userId = 1L;

        doThrow(NotFoundException.class).when(userService).getUserById(userId);

        assertThrows(NotFoundException.class, () -> userController.getUserById(userId));
    }

    @Test
    void getAllUsers_whenUsersCreated_thenReturnUsersListAndEqualsSize() {
        List<UserDto> userDtoList = List.of(new UserDto(), new UserDto(), new UserDto());

        when(userService.getAllUsers()).thenReturn(userDtoList);

        List<UserDto> resultList = userController.getAllUsers();

        assertEquals(resultList.size(), userDtoList.size());
        verify(userService).getAllUsers();
    }

    @Test
    void getAllUsers_whenUsersNotCreated_thenReturnEmptyList() {
        when(userService.getAllUsers()).thenReturn(new ArrayList<>());

        List<UserDto> resultList = userController.getAllUsers();

        assertTrue(resultList.isEmpty());
    }

    @Test
    void createUser_whenUserValid_thenReturnUser() {
        UserDto userDto = new UserDto();
        userDto.setName("testName");

        when(userService.createUser(userDto)).thenReturn(userDto);

        UserDto resultUser = userController.createUser(userDto);

        assertEquals(resultUser.getName(), userDto.getName());
        verify(userService).createUser(userDto);
    }

    @Test
    void updateUser_whenUserFound_thenReturnedUpdatedUser() {
        UserDto oldUserdto = new UserDto();
        oldUserdto.setId(1L);
        oldUserdto.setName("oldName");
        oldUserdto.setEmail("oldEmail@mail.ru");

        when(userService.createUser(oldUserdto)).thenReturn(oldUserdto);
        userController.createUser(oldUserdto);

        UserDto newUserDto = new UserDto();
        newUserDto.setName("newName");
        newUserDto.setEmail("newEmail@mail.ru");

        when(userService.updateUser(newUserDto, oldUserdto.getId())).thenReturn(newUserDto);

        UserDto actualUserDto = userController.updateUser(newUserDto, oldUserdto.getId());

        assertEquals("newName", actualUserDto.getName());
        assertEquals("newEmail@mail.ru", actualUserDto.getEmail());
        verify(userService).updateUser(newUserDto, oldUserdto.getId());
    }

    @Test
    void delete_whenDeleteByUserId_verify() {
        UserDto oldUserdto = new UserDto();
        oldUserdto.setId(1L);
        oldUserdto.setName("name");
        oldUserdto.setEmail("email@mail.ru");

        when(userService.createUser(oldUserdto)).thenReturn(oldUserdto);
        userController.createUser(oldUserdto);

        userController.deleteUser(oldUserdto.getId());

        verify(userService).deleteUser(oldUserdto.getId());
    }
}
