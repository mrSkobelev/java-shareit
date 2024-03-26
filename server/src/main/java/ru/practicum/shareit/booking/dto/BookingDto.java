package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BookingDto {
    private long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
