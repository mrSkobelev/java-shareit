package ru.practicum.shareit.request.service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestsRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestsRepository itemRequestsRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestInfoDto createItemRequest(long userId, ItemRequestDto itemRequestDto) {
        User user = validUser(userId);

        if (itemRequestDto.getDescription().isBlank() || itemRequestDto.getDescription().isEmpty()) {
            throw new ValidationException("Описание запроса должно быть заполнено");
        }

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequester(user);

        ItemRequest savedItemRequest = itemRequestsRepository.save(itemRequest);

        log.info("Создан запрос вещи c id: {}", savedItemRequest.getId());

        return ItemRequestMapper.toItemRequestInfoDto(savedItemRequest, new ArrayList<>());
    }

    @Override
    public List<ItemRequestInfoDto> getRequestsByUserId(long userId) {
        validUser(userId);
        Sort sort = Sort.by(Direction.DESC, "created");

        List<ItemRequest> itemRequestList = itemRequestsRepository.findByRequesterId(userId, sort);
        Map<ItemRequest, List<Item>> itemRequestListMap = createRequestAndItemsMap(itemRequestList);

        log.info("Получен список запросов пользователя с id: {}", userId);

        return itemRequestList.stream()
            .map(itemRequest -> ItemRequestMapper.toItemRequestInfoDto(
                itemRequest, itemRequestListMap.getOrDefault(itemRequest, Collections.emptyList())))
            .collect(toList());
    }

    @Override
    public List<ItemRequestInfoDto> getRequests(long userId, Integer from, Integer size) {
        validUser(userId);
        validPagination(from, size);
        Sort sort = Sort.by(Direction.DESC, "created");
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);

        List<ItemRequest> itemRequestList = itemRequestsRepository.findByRequesterIdNot(userId, pageRequest)
            .getContent();
        Map<ItemRequest, List<Item>> itemRequestListMap = createRequestAndItemsMap(itemRequestList);

        log.info("Получен список запросов для пользователя с id: {}", userId);

        return itemRequestList.stream()
            .map(itemRequest -> ItemRequestMapper.toItemRequestInfoDto(
                itemRequest, itemRequestListMap.getOrDefault(itemRequest, Collections.emptyList())))
            .collect(toList());
    }

    @Override
    public ItemRequestInfoDto getRequestById(long userId, long requestId) {
        validUser(userId);
        ItemRequest itemRequest = validItemRequest(requestId);

        List<Item> items = itemRepository.findByItemRequest(itemRequest);

        log.info("Получена аренда с id: {}", requestId);

        return ItemRequestMapper.toItemRequestInfoDto(itemRequest, items);
    }

    private User validUser(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new DataNotFoundException("Не найден пользователь с id: " + userId);
        }
        return user.get();
    }

    private ItemRequest validItemRequest(long requestId) {
        Optional<ItemRequest> itemRequest = itemRequestsRepository.findById(requestId);
        if (itemRequest.isEmpty()) {
            throw new DataNotFoundException("Не найдена аренда с id: " + requestId);
        }
        return itemRequest.get();
    }

    private Map<ItemRequest, List<Item>> createRequestAndItemsMap(List<ItemRequest> itemRequestList) {
        return itemRepository.findByItemRequestIn(itemRequestList)
            .stream()
            .collect(groupingBy(Item::getItemRequest, toList()));
    }

    private void validPagination(Integer from, Integer size) {
        if (from < 0 || size < 0) {
            throw new ValidationException("Параметры пагинации не должны быть отрицательными");
        }
    }
}
