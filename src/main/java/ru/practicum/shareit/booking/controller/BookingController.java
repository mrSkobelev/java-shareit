package ru.practicum.shareit.booking.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.service.BookingService;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;

    @GetMapping("/{bookingId}")
    public BookingInfoDto getBookingById(@PathVariable long bookingId,
        @RequestHeader("X-Sharer-User-Id") long userId) {

        return service.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingInfoDto> getBookingByUserId(
        @RequestHeader("X-Sharer-User-Id") long userId,
        @RequestParam(name = "state", defaultValue = "ALL") String state,
        @RequestParam(name = "from", defaultValue = "0") Integer from,
        @RequestParam(name = "size", defaultValue = "10") Integer size) {

        return service.getBookingByUserId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingInfoDto> getBookingByOwnerId(
        @RequestHeader("X-Sharer-User-Id") long userId,
        @RequestParam(name = "state", defaultValue = "ALL") String state,
        @RequestParam(name = "from", defaultValue = "0") Integer from,
        @RequestParam(name = "size", defaultValue = "10") Integer size) {

        return service.getBookingByOwnerId(userId, state, from, size);
    }

    @PostMapping
    public BookingInfoDto createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
        @RequestBody BookingDto bookingDto) {

        return service.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingInfoDto approvedBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
            @PathVariable("bookingId") long bookingId,
            @RequestParam(name = "approved") Boolean approved) {

        return service.approveBooking(ownerId, bookingId, approved);
    }
}
