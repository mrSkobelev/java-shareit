package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.ItemDto;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerIT {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

    @Test
    void getItemById_whenItemNotFound_thenStatusNotFound() throws Exception {
        long itemId = 0L;
        long userId = 0L;

        when(itemClient.getItemById(userId, itemId))
            .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/items/{itemId}", itemId)
            .header("X-Sharer-User-Id", userId))
            .andExpect(status().isNotFound());
    }

    @Test
    void createItem_whenFieldsNotValid_thenStatusBadRequest() throws Exception {
        long userId = 0L;
        ItemDto itemDto = new ItemDto();

        when(itemClient.createItem(userId, itemDto))
            .thenReturn(ResponseEntity.badRequest().build());

        mockMvc.perform(post("/items")
            .header("X-Sharer-User-Id", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(itemDto)))
            .andExpect(status().isBadRequest());
    }
}