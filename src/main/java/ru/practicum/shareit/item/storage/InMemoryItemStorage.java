package ru.practicum.shareit.item.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.EmptyFieldsException;
import ru.practicum.shareit.exception.WrongOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

@Repository
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {
    private long generatorId = 1;
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<ItemDto>> itemsByUser = new HashMap<>();

    @Override
    public ItemDto getItemById(long itemId) {
        if (items.containsKey(itemId)) {
            Item item = items.get(itemId);
            return ItemMapper.toItemDto(item);
        }

        throw new DataNotFoundException("Не найдена вещь с id: " + itemId);
    }

    @Override
    public List<ItemDto> getAllItems() {
        List<ItemDto> itemDtoList = new ArrayList<>();

        for (Item i : items.values()) {
            itemDtoList.add(ItemMapper.toItemDto(i));
        }

        return itemDtoList;
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(long userId) {
        return itemsByUser.get(userId);
    }

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        validateEmptyFields(itemDto);

        Item item = ItemMapper.toItem(itemDto);
        item.setId(generateId());
        item.setOwner(userId);

        items.put(item.getId(), item);
        ItemDto newItemDto = ItemMapper.toItemDto(item);

        List<ItemDto> itemDtoList;
        if (itemsByUser.containsKey(userId)) {
            itemDtoList = itemsByUser.get(userId);
        } else {
            itemDtoList = new ArrayList<>();
        }
        itemDtoList.add(newItemDto);
        itemsByUser.put(userId, itemDtoList);

        return newItemDto;
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long userId, long itemId) {
        if (!(items.containsKey(itemId))) {
            throw new DataNotFoundException("Не найдена вещь с id " + itemId);
        }

        Item item = items.get(itemId);

        if (item.getOwner() != userId) {
            throw new WrongOwnerException("Только владелец может редактировать вещь.");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        items.put(itemId, item);

        ItemDto newItemDto = ItemMapper.toItemDto(item);

        List<ItemDto> userItems = getAllItemsByUserId(userId);
        userItems.removeIf(i -> newItemDto.getId() == i.getId());
        userItems.add(newItemDto);
        itemsByUser.put(userId, userItems);

        return newItemDto;
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        List<ItemDto> searchResultList = new ArrayList<>();

        for (Item i : items.values()) {
            String name = i.getName().toLowerCase();
            String description = i.getDescription().toLowerCase();

            if ((name.contains(text.toLowerCase()) || description.contains(text.toLowerCase()))
                && i.getAvailable()) {
                searchResultList.add(ItemMapper.toItemDto(i));
            }
        }

        return searchResultList;
    }

    private long generateId() {
        return generatorId++;
    }

    private void validateEmptyFields(ItemDto itemDto) {
        if (itemDto.getName() == null) {
            throw new EmptyFieldsException("Поля имя должно быть заполнено");
        }

        if (itemDto.getDescription() == null) {
            throw new EmptyFieldsException("Поля описания должно быть заполнено");
        }

        if (itemDto.getAvailable() == null) {
            throw new EmptyFieldsException("Поле доступно должно быть заполнено");
        }
    }
}