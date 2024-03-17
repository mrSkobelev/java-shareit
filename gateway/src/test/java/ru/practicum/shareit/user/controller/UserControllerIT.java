package ru.practicum.shareit.user.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

@WebMvcTest(controllers = UserController.class)
class UserControllerIT {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Test
    void getUserById_whenUserNotFound_thenStatusNotFound() throws Exception {
        long userId = 0L;

        when(userClient.getUserById(userId))
            .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/users/{userId}", userId))
            .andExpect(status().isNotFound());
    }

    @Test
    void createUser_whenFieldsNotValid_thenStatusBadRequest() throws Exception {
        UserDto userDto = new UserDto();

        when(userClient.createUser(userDto))
            .thenReturn(ResponseEntity.badRequest().build());

        mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
            .andExpect(status().isBadRequest());
    }
}