package ru.practicum.shareit.item.service;

import java.util.List;
import ru.practicum.shareit.item.dto.ItemDto;

public interface ItemService {
    ItemDto getItemById(long id);

    List<ItemDto> getAllItems();
    List<ItemDto> getAllItemsByUserId(long userId);

    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(ItemDto itemDto, long userId, long itemId);

    List<ItemDto> searchItem(String text);
}
