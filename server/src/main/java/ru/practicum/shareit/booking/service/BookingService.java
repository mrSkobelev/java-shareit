package ru.practicum.shareit.booking.service;

import java.util.List;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;

public interface BookingService {
    BookingInfoDto getBookingById(long bookingId, long userId);

    List<BookingInfoDto> getBookingByUserId(long bookerId, String stateParameter, Integer from, Integer size);

    List<BookingInfoDto> getBookingByOwnerId(long ownerId, String stateParameter, Integer from, Integer size);

    BookingInfoDto createBooking(long userId, BookingDto bookingDto);


    BookingInfoDto approveBooking(long ownerId, long bookingId, Boolean approved);
}
