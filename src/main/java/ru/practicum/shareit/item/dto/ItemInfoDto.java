package ru.practicum.shareit.item.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ItemInfoDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentInfoDto> comments;
    private BookingInfoDto lastBooking;
    private BookingInfoDto nextBooking;

    @Data
    @AllArgsConstructor
    public static class BookingInfoDto {
        Long id;
        Long bookerId;
    }
}
