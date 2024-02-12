package ru.practicum.shareit.request.model;

import java.time.LocalDateTime;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
public class ItemRequest {
    private int id;
    private String description;
    private User requester;
    private LocalDateTime created = LocalDateTime.now();
}
