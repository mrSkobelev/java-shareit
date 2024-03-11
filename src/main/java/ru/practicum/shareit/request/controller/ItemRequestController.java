package ru.practicum.shareit.request.controller;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.service.ItemRequestService;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestInfoDto createItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
        @Valid @RequestBody ItemRequestDto itemRequestDto) {

        return itemRequestService.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestInfoDto> getRequestsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {

        return itemRequestService.getRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestInfoDto> getRequests(
        @RequestHeader("X-Sharer-User-Id") long userId,
        @RequestParam(name = "from", defaultValue = "0") Integer from,
        @RequestParam(name = "size", defaultValue = "10") Integer size) {

        return itemRequestService.getRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestInfoDto getRequestById(
        @RequestHeader("X-Sharer-User-Id") long userId,
        @PathVariable long requestId) {

        return itemRequestService.getRequestById(userId, requestId);
    }
}
