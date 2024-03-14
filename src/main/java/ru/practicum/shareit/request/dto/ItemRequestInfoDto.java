package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

@Data
public class ItemRequestInfoDto {
    private long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}
