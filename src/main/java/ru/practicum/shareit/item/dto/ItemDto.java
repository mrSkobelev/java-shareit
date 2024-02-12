package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ItemDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private Boolean available;
}