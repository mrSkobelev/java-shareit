package ru.practicum.shareit.booking.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    @InjectMocks
    private BookingController bookingController;
    @Mock
    private BookingService bookingService;

    @Test
    void getBookingById_whenBookingCreated_thenReturnBooking() {
        long userId = 0L;
        long bookingId = 0L;

        Booking booking = new Booking();
        BookingInfoDto bookingInfoDto = new BookingInfoDto();
        User booker = new User();
        Item item = new Item();
        ItemDto itemDto = new ItemDto();
        UserDto userDto = new UserDto();

        booking.setBooker(booker);
        booking.setItem(item);

        bookingInfoDto.setBooker(userDto);
        bookingInfoDto.setItem(itemDto);

        BookingInfoDto bookingMapperBookingInfoDto = BookingMapper.toBookingInfoDto(booking);

        when(bookingService.getBookingById(userId, bookingId)).thenReturn(bookingMapperBookingInfoDto);

        BookingInfoDto actualBookingInfoDto = bookingController.getBookingById(userId, bookingId);

        assertEquals(bookingMapperBookingInfoDto, actualBookingInfoDto);
    }

    @Test
    void getBookingByUserId_whenBookingCreated_thenReturnList() {
        long userId = 1L;
        Booking booking = new Booking();
        User booker = new User();
        Item item = new Item();

        booking.setBooker(booker);
        booking.setItem(item);

        BookingInfoDto bookingInfoDto = BookingMapper.toBookingInfoDto(booking);
        List<BookingInfoDto> bookingInfoDtoList = List.of(bookingInfoDto);

        when(bookingService.getBookingByUserId(anyLong(), anyString(),
            anyInt(), anyInt())).thenReturn(bookingInfoDtoList);

        List<BookingInfoDto> resultBookingInfoDtoList = bookingController
            .getBookingByUserId(userId, "all", 0, 5);

        assertEquals(bookingInfoDtoList.size(), resultBookingInfoDtoList.size());
    }

    @Test
    void getBookingByOwnerId_whenBookingsCreated_thenReturnList() {
        long userId = 0L;
        Booking booking = new Booking();
        User booker = new User();
        Item item = new Item();

        booking.setBooker(booker);
        booking.setItem(item);

        BookingInfoDto bookingInfoDto = BookingMapper.toBookingInfoDto(booking);
        List<BookingInfoDto> bookingInfoDtoList = List.of(bookingInfoDto);

        when(bookingService.getBookingByOwnerId(anyLong(), anyString(),
            anyInt(), anyInt())).thenReturn(bookingInfoDtoList);

        List<BookingInfoDto> resultBookingInfoDtoList = bookingController
            .getBookingByOwnerId(userId, "all", 0, 5);

        assertEquals(bookingInfoDtoList.size(), resultBookingInfoDtoList.size());
    }

    @Test
    void createBooking_whenBookingValid_thenReturnBooking() {
        long bookerId = 1L;
        BookingDto bookingDto = new BookingDto();
        Booking booking = new Booking();
        User booker = new User();
        booking.setBooker(booker);
        Item item = new Item();
        booking.setItem(item);
        BookingInfoDto bookingInfoDto = BookingMapper.toBookingInfoDto(booking);

        when(bookingService.createBooking(bookerId, bookingDto)).thenReturn(bookingInfoDto);

        BookingInfoDto actualBookingInfoDto = bookingController.createBooking(bookerId, bookingDto);

        assertEquals(bookingInfoDto, actualBookingInfoDto);
    }

    @Test
    void approvedBooking_whenBookingCreated_thenReturnApprovedBooking() {
        long ownerId = 0L;
        long bookingId = 0L;
        Boolean approved = true;

        Booking booking = new Booking();
        User booker = new User();
        UserDto bookerDto = new UserDto();
        Item item = new Item();

        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);

        BookingInfoDto bookingInfoDto = new BookingInfoDto();
        bookingInfoDto.setBooker(bookerDto);
        bookingInfoDto.setStatus(booking.getStatus());

        BookingInfoDto mapperBookingInfoDto = BookingMapper.toBookingInfoDto(booking);

        when(bookingService.approveBooking(ownerId, bookingId, approved)).thenReturn(mapperBookingInfoDto);

        BookingInfoDto actualBookingInfoDto = bookingController.approvedBooking(ownerId, bookingId, approved);

        assertEquals(mapperBookingInfoDto, actualBookingInfoDto);
    }
}