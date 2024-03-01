package ru.practicum.shareit.item.storage;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @EntityGraph(value = "Item.UsersAndRequests")
    List<Item> findByOwnerId(long userId);

    @EntityGraph(value = "Item.UsersAndRequests")
    @Query(" select i from Item i " +
        "where upper(i.name) like upper(concat('%', ?1, '%')) " +
        " or upper(i.description) like upper(concat('%', ?1, '%')) " +
        "and available = true")
    List<Item> searchItemByText(String text);

}
