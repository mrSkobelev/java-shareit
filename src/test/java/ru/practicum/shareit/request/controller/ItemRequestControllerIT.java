package ru.practicum.shareit.request.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.service.ItemRequestService;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerIT {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ItemRequestService itemRequestService;

    @SneakyThrows
    @Test
    void createItemRequest_whenRequestValid_thenStatusOk() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("description");
        ItemRequestInfoDto itemRequestInfoDto = new ItemRequestInfoDto();

        when(itemRequestService.createItemRequest(anyLong(), any(ItemRequestDto.class))).thenReturn(itemRequestInfoDto);

        String result = mockMvc.perform(post("/requests")
                .header("X-Sharer-User-Id", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemRequestDto)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestInfoDto), result);
    }

    @SneakyThrows
    @Test
    void getRequestsByUserId_whenUserCreateRequest_thenStatusOk() {
        ItemRequestInfoDto itemRequestInfoDto = new ItemRequestInfoDto();
        List<ItemRequestInfoDto> itemRequestInfoDtoList = List.of(itemRequestInfoDto);

        when(itemRequestService.getRequestsByUserId(anyLong())).thenReturn(itemRequestInfoDtoList);

        String result = mockMvc.perform(get("/requests")
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestInfoDtoList), result);
    }

    @SneakyThrows
    @Test
    void getRequests_whenRequestsCreated_thenStatusOk() {
        int from = 0;
        int size = 1;
        ItemRequestInfoDto itemRequestInfoDto = new ItemRequestInfoDto();
        List<ItemRequestInfoDto> itemRequestInfoDtoList = List.of(itemRequestInfoDto);

        when(itemRequestService.getRequests(anyLong(), anyInt(), anyInt()))
            .thenReturn(itemRequestInfoDtoList);

        String result = mockMvc.perform(get("/requests/all")
                .header("X-Sharer-User-Id", 1)
                .param("from", objectMapper.writeValueAsString(from))
                .param("size", objectMapper.writeValueAsString(size)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestInfoDtoList), result);

    }

    @SneakyThrows
    @Test
    void getRequestById() {
        long requestId = 1L;
        ItemRequestInfoDto itemRequestInfoDto = new ItemRequestInfoDto();

        when(itemRequestService.getRequestById(anyLong(), anyLong())).thenReturn(itemRequestInfoDto);

        String result = mockMvc.perform(get("/requests/{requestId}", requestId)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestInfoDto), result);
    }
}