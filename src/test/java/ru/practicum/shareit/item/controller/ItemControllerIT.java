package ru.practicum.shareit.item.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerIT {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemService itemService;

    @SneakyThrows
    @Test
    void getItemById_whenItemCreated_thenReturnItem() {
        long itemId = 1L;
        long userId = 1L;
        ItemInfoDto itemInfoDto = new ItemInfoDto();

        when(itemService.getItemById(userId, itemId)).thenReturn(itemInfoDto);

        String result = mockMvc.perform(get("/items/{itemId}", itemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemInfoDto))
                .header("X-Sharer-User-Id", userId))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(result, objectMapper.writeValueAsString(itemInfoDto));
    }

    @SneakyThrows
    @Test
    void getAllItemsByUserId_whenItemsCreated_thenReturnItemList() {
        List<ItemInfoDto> itemInfoDtoList = List.of(new ItemInfoDto());

        when(itemService.getAllItemsByUserId(anyLong(), anyInt(), anyInt()))
            .thenReturn(itemInfoDtoList);

        String result = mockMvc.perform(get("/items")
                .contentType("application/json")
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemInfoDtoList), result);
    }

    @SneakyThrows
    @Test
    void createItem_whenItemIsValid_thenReturnItemAndEqualsFields() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);

        when(itemService.createItem(anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        String result = mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto))
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(itemDto.getId()))
            .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
            .andExpect(jsonPath("$.name").value(itemDto.getName()))
            .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(result, objectMapper.writeValueAsString(itemDto));
    }

    @SneakyThrows
    @Test
    void updateItem_whenItemIsValid_thenReturnUpdatedItem() {
        long itemId = 0L;
        ItemDto itemDto = new ItemDto();

        when(itemService.updateItem(any(ItemDto.class), anyLong(), anyLong())).thenReturn(itemDto);

        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(itemDto))
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @SneakyThrows
    @Test
    void searchItem_whenTextIsPresent_thenReturnItemList() {
        String text = "text";
        int from = 0;
        int size = 1;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("text");
        itemDto.setDescription("text");
        List<ItemDto> itemDtoList = List.of(itemDto);

        when(itemService.searchItem(anyString(), anyInt(), anyInt()))
            .thenReturn(itemDtoList);

        String result = mockMvc.perform(get("/items/search")
                .param("text", text)
                .param("from", objectMapper.writeValueAsString(from))
                .param("size", objectMapper.writeValueAsString(size))
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(result, objectMapper.writeValueAsString(itemDtoList));
    }

    @SneakyThrows
    @Test
    void addComment_whenCommentValid_thenReturnComment() {
        CommentInfoDto commentInfoDto = new CommentInfoDto();
        commentInfoDto.setText("test");

        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
            .thenReturn(commentInfoDto);

        String result = mockMvc.perform(post("/items/{itemId}/comment", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentInfoDto))
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentInfoDto), result);
    }
}