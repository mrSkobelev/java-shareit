package ru.practicum.shareit.item.storage;

import java.util.List;
import ru.practicum.shareit.item.dto.ItemDto;

public interface ItemStorage {
    ItemDto getItemById(long itemId);
    List<ItemDto> getAllItems();
    List<ItemDto> getAllItemsByUserId(long userId);
    ItemDto createItem(long userId, ItemDto itemDto);
    ItemDto updateItem(ItemDto itemDto, long userId, long ItemId);
    List<ItemDto> searchItem(String text);
}
