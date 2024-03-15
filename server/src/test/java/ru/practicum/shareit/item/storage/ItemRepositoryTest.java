package ru.practicum.shareit.item.storage;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestsRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestsRepository itemRequestRepository;
    private User owner;
    private User requester;
    private Item item;
    private ItemRequest itemRequest;
    private List<ItemRequest> itemRequests;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("owner");
        owner.setEmail("ownerEmail@mail.com");
        userRepository.save(owner);

        requester = new User();
        requester.setName("requester");
        requester.setEmail("requesterEmail@mail.com");
        userRepository.save(requester);

        itemRequest = new ItemRequest();
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("requestDescription");
        itemRequests = List.of(itemRequest);
        itemRequestRepository.save(itemRequest);

        item = new Item();
        item.setName("item");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setItemRequest(itemRequest);
        itemRepository.save(item);
    }

    @Test
    void findByOwnerId_whenOwnerIsPresent_thenReturnItemList() {
        List<Item> items = itemRepository
            .findByOwnerId(owner.getId(), PageRequest.of(0, 1))
            .getContent();

        assertEquals(1, items.size());
    }

    @Test
    void searchItemByText_whenTextIsPresent_thenReturnItemList() {
        List<Item> items = itemRepository
            .searchItemByText("item", PageRequest.of(0, 1))
            .getContent();

        assertEquals(1, items.size());
    }

    @Test
    void findByItemRequestIn_whenItemRequestListIsCreated_thenReturnItemList() {
        List<Item> items = itemRepository.findByItemRequestIn(itemRequests);

        assertEquals(1, items.size());
    }

    @Test
    void findByItemRequest_whenItemRequestIsPresent_thenReturnItemList() {
        List<Item> items = itemRepository.findByItemRequest(itemRequest);

        assertEquals("item", items.get(0).getName());
    }

    @AfterEach
    void deleteAllItem() {
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }
}