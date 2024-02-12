package ru.practicum.shareit.item.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto getItemById(long itemId) {
        log.info("Получить товар с id = {}", itemId);
        return itemStorage.getItemById(itemId);
    }

    @Override
    public List<ItemDto> getAllItems() {
        log.info("Получить все товары");
        return itemStorage.getAllItems();
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(long userId) {
        log.info("Получить все товары пользователя с id = {}", userId);
        userStorage.getUserById(userId);
        return itemStorage.getAllItemsByUserId(userId);
    }

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        log.info("Создать товар пользователя с id = {}", userId);
        userStorage.getUserById(userId);
        return itemStorage.createItem(userId, itemDto);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long userId, long itemId) {
        log.info("Обновить товар с id = {}", itemId);
        userStorage.getUserById(userId);
        return itemStorage.updateItem(itemDto, userId, itemId);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        log.info("Поиск товара по значению {}", text.toUpperCase());
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.searchItem(text);
    }
}