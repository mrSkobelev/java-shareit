package ru.practicum.shareit.request.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    @InjectMocks
    private ItemRequestController itemRequestController;
    @Mock
    private ItemRequestService itemRequestService;

    @Test
    void createItemRequest_whenRequestValid_thenReturnRequest() {
        long requesterId = 1L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setId(1L);
        ItemRequestInfoDto itemRequestInfoDto = ItemRequestMapper.toItemRequestInfoDto(itemRequest, new ArrayList<>());

        when(itemRequestService.createItemRequest(requesterId, itemRequestDto)).thenReturn(itemRequestInfoDto);

        ItemRequestInfoDto actualItemRequestInfoDto = itemRequestController.createItemRequest(requesterId, itemRequestDto);

        assertEquals(itemRequestInfoDto, actualItemRequestInfoDto);
    }

    @Test
    void getRequestsByUserId_whenUserCreateRequest_thenReturnUser() {
        long requesterId = 1L;
        List<ItemRequestInfoDto> itemRequestInfoDtoList = List.of(new ItemRequestInfoDto());

        when(itemRequestService.getRequestsByUserId(requesterId)).thenReturn(itemRequestInfoDtoList);

        List<ItemRequestInfoDto> resultItemRequestInfoDtoList = itemRequestController.getRequestsByUserId(requesterId);

        assertEquals(itemRequestInfoDtoList.size(), resultItemRequestInfoDtoList.size());
    }

    @Test
    void getRequests_whenListFound_thenReturnList() {
        long requesterId = 1L;
        Integer from = 0;
        Integer size = 1;
        List<ItemRequestInfoDto> itemRequestInfoDtoList = List.of(new ItemRequestInfoDto());

        when(itemRequestService.getRequests(requesterId, from, size))
            .thenReturn(itemRequestInfoDtoList);

        List<ItemRequestInfoDto> resultItemRequestInfoDtoList = itemRequestController
            .getRequests(requesterId, from, size);

        assertEquals(itemRequestInfoDtoList.size(), resultItemRequestInfoDtoList.size());
    }

    @Test
    void getRequestById_whenRequestCreated_thenReturnRequest() {
        long userId = 1L;
        long requestId = 1L;
        ItemRequestInfoDto itemRequestInfoDto = new ItemRequestInfoDto();

        when(itemRequestService.getRequestById(userId, requestId)).thenReturn(itemRequestInfoDto);

        ItemRequestInfoDto actualItemRequestInfoDto = itemRequestController
            .getRequestById(userId, requestId);

        assertEquals(itemRequestInfoDto, actualItemRequestInfoDto);
    }
}