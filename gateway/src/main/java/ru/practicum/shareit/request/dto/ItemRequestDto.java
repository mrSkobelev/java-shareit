package ru.practicum.shareit.request.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ItemRequestDto {
    private long id;
    @NotBlank
    private String description;
}
