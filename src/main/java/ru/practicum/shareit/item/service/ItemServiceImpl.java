package ru.practicum.shareit.item.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto getItemById(long itemId) {
        return itemStorage.getItemById(itemId);
    }

    @Override
    public List<ItemDto> getAllItems() {
        return itemStorage.getAllItems();
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(long userId) {
        userStorage.getUserById(userId);
        return itemStorage.getAllItemsByUserId(userId);
    }

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        userStorage.getUserById(userId);
        return itemStorage.createItem(userId, itemDto);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long userId, long itemId) {
        userStorage.getUserById(userId);
        return itemStorage.updateItem(itemDto, userId, itemId);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.searchItem(text);
    }
}