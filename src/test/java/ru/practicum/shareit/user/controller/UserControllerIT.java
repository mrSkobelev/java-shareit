package ru.practicum.shareit.user.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@WebMvcTest(controllers = UserController.class)
class UserControllerIT {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @SneakyThrows
    @Test
    void getUserById_whenUserIsCreated_thenStatusOk() {
        long userId = 1L;

        mockMvc.perform(get("/users/{userId}", userId))
            .andExpect(status().isOk());

        verify(userService).getUserById(userId);
    }

    @SneakyThrows
    @Test
    void getAllUsers_whenUsersCreated_thenReturnUsersListAndStatusOk() {
        UserDto userDto = new UserDto();
        userDto.setName("name");
        userDto.setEmail("email@mail.com");
        List<UserDto> userDtoList = List.of(userDto);

        when(userService.getAllUsers()).thenReturn(userDtoList);

        String result = mockMvc.perform(get("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDtoList)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]email").value(userDto.getEmail()))
            .andExpect(jsonPath("$[0]name").value(userDto.getName()))
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDtoList), result);
    }

    @SneakyThrows
    @Test
    void createUser_whenUserIsValid_thenReturnSavedUserAndStatusOk() {
        UserDto userDto = new UserDto();
        userDto.setName("name");
        userDto.setEmail("email@mail.com");

        when(userService.createUser(userDto)).thenReturn(userDto);

        String result = mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userDto)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
        verify(userService).createUser(userDto);
    }

    @SneakyThrows
    @Test
    void createUser_whenUserEmailIsNotValid_thenStatusBadRequest() {
        UserDto userDto = new UserDto();
        userDto.setName("name");
        userDto.setEmail("emailmail.com");

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
            .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(userDto);
    }

    @SneakyThrows
    @Test
    void updateUser_whenUserEmailIsNotValid_thenStatusBadRequest() {
        long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setEmail("email.ru");

        mockMvc.perform(patch("/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
            .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(userDto, userId);
    }

    @SneakyThrows
    @Test
    void updateUser_whenUserIsValid_thenReturnUpdatedUserAndStatusOk() {
        long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setName("newName");
        when(userService.updateUser(userDto, userId)).thenReturn(userDto);

        String result = mockMvc.perform(patch("/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userService.updateUser(userDto, userId)), result);
    }

    @SneakyThrows
    @Test
    void deleteUser_ifUserIsCreated_thenStatusOk() {
        long userId = 1L;

        mockMvc.perform(delete("/users/{userId}", userId))
            .andExpect(status().isOk());

        verify(userService).deleteUser(userId);
    }
}