package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CommentInfoDto {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
