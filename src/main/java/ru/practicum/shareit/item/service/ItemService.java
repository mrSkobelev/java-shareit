package ru.practicum.shareit.item.service;

import java.util.List;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;

public interface ItemService {
    ItemInfoDto getItemById(long userId, long itemId);

    List<ItemInfoDto> getAllItemsByUserId(long userId);

    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(ItemDto itemDto, long userId, long itemId);

    List<ItemDto> searchItem(String text);

    CommentInfoDto addComment(long userId, long itemId, CommentDto commentDto);
}
