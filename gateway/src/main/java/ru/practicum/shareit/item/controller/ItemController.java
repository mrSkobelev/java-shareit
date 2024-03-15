package ru.practicum.shareit.item.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
        @PathVariable("itemId") long itemId) {

        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUserId(
        @RequestHeader("X-Sharer-User-Id") long userId,
        @RequestParam(name = "from", defaultValue = "0") Integer from,
        @RequestParam(name = "size", defaultValue = "10") Integer size) {

        return itemClient.getAllItemsByUserId(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
        @Valid @RequestBody ItemDto itemDto) {

        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemDto itemDto,
                                @PathVariable("itemId") long itemId) {

        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text,
        @RequestParam(name = "from", defaultValue = "0") Integer from,
        @RequestParam(name = "size", defaultValue = "10") Integer size) {

        return itemClient.searchItem(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable("itemId") long itemId,
                    @RequestBody CommentDto commentDto) {

        return itemClient.addComment(userId, itemId, commentDto);
    }
}