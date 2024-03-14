package ru.practicum.shareit.item.storage;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @EntityGraph(value = "Item.UsersAndRequests")
    Page<Item> findByOwnerId(long userId, PageRequest pageRequest);

    @EntityGraph(value = "Item.UsersAndRequests")
    @Query(" select i from Item i " +
        "where upper(i.name) like upper(concat('%', ?1, '%')) " +
        " or upper(i.description) like upper(concat('%', ?1, '%')) " +
        "and available = true")
    Page<Item> searchItemByText(String text, PageRequest pageRequest);

    List<Item> findByItemRequestIn(List<ItemRequest> itemRequestList);

    List<Item> findByItemRequest(ItemRequest itemRequest);
}
