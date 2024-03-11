package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dto.UserMapper;

@UtilityClass
public class BookingMapper {
    public BookingInfoDto toBookingInfoDto(Booking booking) {
        BookingInfoDto bookingInfoDto = new BookingInfoDto();

        bookingInfoDto.setId(booking.getId());
        bookingInfoDto.setStart(booking.getStart());
        bookingInfoDto.setEnd(booking.getEnd());
        bookingInfoDto.setStatus(booking.getStatus());
        bookingInfoDto.setBooker(UserMapper.toUserDto(booking.getBooker()));
        bookingInfoDto.setItem(ItemMapper.toItemDto(booking.getItem()));

        return bookingInfoDto;
    }

    public Booking toBooking(BookingDto bookingDto) {
        Booking booking = new Booking();

        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());

        return booking;
    }
}
