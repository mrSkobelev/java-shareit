package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();

        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());

        return itemRequest;
    }

    public ItemRequestInfoDto toItemRequestInfoDto(ItemRequest itemRequest, List<Item> items) {
        ItemRequestInfoDto itemRequestInfoDto = new ItemRequestInfoDto();

        itemRequestInfoDto.setId(itemRequest.getId());
        itemRequestInfoDto.setDescription(itemRequest.getDescription());
        itemRequestInfoDto.setCreated(itemRequest.getCreated());

        List<ItemDto> itemDtoList = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());

        itemRequestInfoDto.setItems(itemDtoList);

        return itemRequestInfoDto;
    }
}
