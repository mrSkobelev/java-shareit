package ru.practicum.shareit.request.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

@DataJpaTest
class ItemRequestsRepositoryIT {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestsRepository itemRequestsRepository;
    private User firstRequester;
    private User secondRequester;
    private ItemRequest firstItemRequest;
    private ItemRequest secondItemRequest;

    @BeforeEach
    void setUp() {
        firstRequester = new User();
        firstRequester.setName("first");
        firstRequester.setEmail("first@mail.ru");
        userRepository.save(firstRequester);

        secondRequester = new User();
        secondRequester.setName("second");
        secondRequester.setEmail("second@mail.ru");
        userRepository.save(secondRequester);

        firstItemRequest = new ItemRequest();
        firstItemRequest.setRequester(firstRequester);
        firstItemRequest.setCreated(LocalDateTime.now());
        firstItemRequest.setDescription("firstRequest");
        itemRequestsRepository.save(firstItemRequest);

        secondItemRequest = new ItemRequest();
        secondItemRequest.setRequester(secondRequester);
        secondItemRequest.setCreated(LocalDateTime.now());
        secondItemRequest.setDescription("secondRequest");
        itemRequestsRepository.save(secondItemRequest);
    }

    @Test
    void findByRequesterId() {
        List<ItemRequest> itemRequestList = itemRequestsRepository
            .findByRequesterId(firstRequester.getId(), Sort.by(Sort.Direction.DESC, "created"));

        assertEquals(1, itemRequestList.size());
    }

    @Test
    void findByRequesterIdNot() {
        List<ItemRequest> itemRequestList = itemRequestsRepository
            .findByRequesterIdNot(firstRequester.getId(), PageRequest.of(0, 1))
            .getContent();

        assertEquals("second", itemRequestList.get(0).getRequester().getName());
    }

    @AfterEach
    void deleteAllItem() {
        itemRequestsRepository.deleteAll();
        userRepository.deleteAll();
    }
}