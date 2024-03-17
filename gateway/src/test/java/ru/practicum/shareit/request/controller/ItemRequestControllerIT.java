package ru.practicum.shareit.request.controller;

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
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerIT {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RequestClient requestClient;

    @Test
    void getItemRequestById_whenRequestNotFound_thenStatusNotFound() throws Exception {
        long requestId = 0L;
        long userId = 0L;

        when(requestClient.getRequestById(userId, requestId))
            .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/requests/{requestId}", requestId)
            .header("X-Sharer-User-Id", userId))
            .andExpect(status().isNotFound());
    }

    @Test
    void createItemRequest_whenFieldsNotValid_thenStatusBadRequest() throws Exception {
        long userId = 0L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();

        when(requestClient.createItemRequest(userId, itemRequestDto))
            .thenReturn(ResponseEntity.badRequest().build());

        mockMvc.perform(post("/requests")
            .header("X-Sharer-User-Id", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(itemRequestDto)))
            .andExpect(status().isBadRequest());
    }
}