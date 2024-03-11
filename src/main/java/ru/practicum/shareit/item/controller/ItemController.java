package ru.practicum.shareit.item.controller;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @GetMapping("/{itemId}")
    public ItemInfoDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
        @PathVariable("itemId") long itemId) {

        return service.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemInfoDto> getAllItemsByUserId(
        @RequestHeader("X-Sharer-User-Id") long userId,
        @RequestParam(name = "from", defaultValue = "0") Integer from,
        @RequestParam(name = "size", defaultValue = "10") Integer size) {

        return service.getAllItemsByUserId(userId, from, size);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId,
        @Valid @RequestBody ItemDto itemDto) {

        return service.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemDto itemDto,
                                @PathVariable("itemId") long itemId) {

        return service.updateItem(itemDto, userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text,
        @RequestParam(name = "from", defaultValue = "0") Integer from,
        @RequestParam(name = "size", defaultValue = "10") Integer size) {

        return service.searchItem(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentInfoDto addComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable("itemId") long itemId,
                    @RequestBody CommentDto commentDto) {

        return service.addComment(userId, itemId, commentDto);
    }
}