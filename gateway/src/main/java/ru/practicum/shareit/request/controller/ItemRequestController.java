package ru.practicum.shareit.request.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
        @Valid @RequestBody ItemRequestDto itemRequestDto) {

        return requestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {

        return requestClient.getRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequests(
        @RequestHeader("X-Sharer-User-Id") long userId,
        @RequestParam(name = "from", defaultValue = "0") Integer from,
        @RequestParam(name = "size", defaultValue = "10") Integer size) {

        return requestClient.getRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
        @RequestHeader("X-Sharer-User-Id") long userId,
        @PathVariable long requestId) {

        return requestClient.getRequestById(userId, requestId);
    }
}
