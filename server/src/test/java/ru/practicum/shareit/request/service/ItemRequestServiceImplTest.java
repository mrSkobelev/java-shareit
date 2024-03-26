package ru.practicum.shareit.request.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestsRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRequestsRepository itemRequestsRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    void createItemRequest_whenRequestValid_thenReturnRequest() {
        long userId = 1L;
        User requester = new User();
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("description");
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setId(1L);
        itemRequest.setRequester(requester);
        ItemRequestInfoDto itemRequestInfoDto = ItemRequestMapper.toItemRequestInfoDto(itemRequest, new ArrayList<>());

        when(userRepository.findById(userId)).thenReturn(Optional.of(requester));
        when(itemRequestsRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestInfoDto actualItemRequestInfoDto = itemRequestService.createItemRequest(userId, itemRequestDto);

        assertEquals(itemRequestInfoDto, actualItemRequestInfoDto);
    }

    @Test
    void getRequestsByUserId_whenUserCreateRequest_thenReturnList() {
        long requesterId = 0L;

        User requester = new User();
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        List<ItemRequest> itemRequests = List.of(itemRequest);
        Item item = new Item();
        item.setItemRequest(itemRequest);
        List<Item> items = List.of(item);
        ItemRequestInfoDto itemRequestInfoDto = ItemRequestMapper.toItemRequestInfoDto(itemRequest, items);
        List<ItemRequestInfoDto> itemRequestInfoDtoList = List.of(itemRequestInfoDto);

        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(itemRequestsRepository.findByRequesterId(requesterId, sort))
            .thenReturn(itemRequests);
        when(itemRepository.findByItemRequestIn(itemRequests)).thenReturn(items);

        List<ItemRequestInfoDto> resultItemRequestInfoDtoList = itemRequestService.getRequestsByUserId(requesterId);

        assertEquals(itemRequestInfoDtoList, resultItemRequestInfoDtoList);
    }

    @Test
    void getRequests_whenRequestsCreated_thenReturnList() {
        long userId = 0L;
        int from = 0;
        int size = 1;
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        List<ItemRequest> itemRequests = List.of(itemRequest);
        Page<ItemRequest> itemRequestPage = new PageImpl<>(itemRequests, pageRequest, itemRequests.size());
        Item item = new Item();
        item.setItemRequest(itemRequest);
        List<Item> items = List.of(item);
        ItemRequestInfoDto itemRequestInfoDto = ItemRequestMapper.toItemRequestInfoDto(itemRequest, items);
        List<ItemRequestInfoDto> itemRequestInfoDtoList = List.of(itemRequestInfoDto);

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRequestsRepository.findByRequesterIdNot(userId, pageRequest)).thenReturn(itemRequestPage);
        when(itemRepository.findByItemRequestIn(itemRequests)).thenReturn(items);

        List<ItemRequestInfoDto> resultItemRequestInfoDtoList = itemRequestService
            .getRequests(userId, from, size);

        assertEquals(itemRequestInfoDtoList, resultItemRequestInfoDtoList);
    }

    @Test
    void getRequestById_whenRequestCreated_thenReturnRequest() {
        long userId = 0L;
        User requester = new User();
        long requestId = 0L;
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        List<Item> items = List.of(new Item());
        ItemRequestInfoDto itemRequestInfoDto = ItemRequestMapper.toItemRequestInfoDto(itemRequest, items);

        when(userRepository.findById(userId)).thenReturn(Optional.of(requester));
        when(itemRequestsRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when((itemRepository.findByItemRequest(itemRequest))).thenReturn(items);

        ItemRequestInfoDto actualItemRequestInfoDto = itemRequestService.getRequestById(userId, requestId);

        assertEquals(itemRequestInfoDto, actualItemRequestInfoDto);
    }
}