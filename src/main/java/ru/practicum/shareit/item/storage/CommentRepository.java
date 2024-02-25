package ru.practicum.shareit.item.storage;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph(value = "Comment.ItemAndUser")
    List<Comment> findByItem_Id(Long itemId);

    @EntityGraph(value = "Comment.ItemAndUser")
    List<Comment> findByItemIn(List<Item> items, Sort sort);
}
