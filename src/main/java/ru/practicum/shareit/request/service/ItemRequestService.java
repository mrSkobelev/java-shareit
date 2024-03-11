package ru.practicum.shareit.request.service;

import java.util.List;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;

public interface ItemRequestService {
    ItemRequestInfoDto createItemRequest(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestInfoDto> getRequestsByUserId(long userId);

    List<ItemRequestInfoDto> getRequests(long userId, Integer from, Integer size);

    ItemRequestInfoDto getRequestById(long userId, long requestId);
}
