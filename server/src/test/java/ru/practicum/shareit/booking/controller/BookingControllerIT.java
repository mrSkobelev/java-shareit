package ru.practicum.shareit.booking.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.service.BookingService;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerIT {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @SneakyThrows
    @Test
    void getBookingById_whenBookingCreated_thenStatusOk() {
        long bookingId = 0L;
        BookingInfoDto bookingInfoDto = new BookingInfoDto();

        when(bookingService.getBookingById(anyLong(), anyLong()))
            .thenReturn(bookingInfoDto);

        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingInfoDto), result);
    }

    @SneakyThrows
    @Test
    void getBookingByUserId_whenParametersIsValid_thenStatusOk() {
        String state = "all";
        int from = 0;
        int size = 1;
        BookingInfoDto bookingInfoDto = new BookingInfoDto();
        List<BookingInfoDto> bookingInfoDtoList = List.of(bookingInfoDto);

        when(bookingService.getBookingByUserId(anyLong(), anyString(), anyInt(), anyInt()))
            .thenReturn(bookingInfoDtoList);

        String result = mockMvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", 1)
                .param("state", state)
                .param("from", objectMapper.writeValueAsString(from))
                .param("size", objectMapper.writeValueAsString(size)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingInfoDtoList), result);

    }

    @SneakyThrows
    @Test
    void getBookingByOwnerId_whenParametersIsValid_thenStatusOk() {
        String state = "all";
        int from = 0;
        int size = 1;
        BookingInfoDto bookingInfoDto = new BookingInfoDto();
        List<BookingInfoDto> bookingInfoDtoList = List.of(bookingInfoDto);

        when(bookingService.getBookingByOwnerId(anyLong(), anyString(), anyInt(), anyInt()))
            .thenReturn(bookingInfoDtoList);

        String result = mockMvc.perform(get("/bookings/owner")
                .header("X-Sharer-User-Id", 1)
                .param("state", state)
                .param("from", objectMapper.writeValueAsString(from))
                .param("size", objectMapper.writeValueAsString(size)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingInfoDtoList), result);

    }

    @SneakyThrows
    @Test
    void createBooking_whenBookingIsValid_thenStatusOk() {
        BookingInfoDto bookingInfoDto = new BookingInfoDto();
        BookingDto bookingDto = new BookingDto();
        LocalDateTime now = LocalDateTime.now();
        bookingDto.setStart(now.plusSeconds(2));
        bookingDto.setEnd(bookingDto.getStart().plusSeconds(5));

        when(bookingService.createBooking(anyLong(), any(BookingDto.class)))
            .thenReturn(bookingInfoDto);

        String result = mockMvc.perform(post("/bookings")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(bookingDto))
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingInfoDto), result);
    }

    @SneakyThrows
    @Test
    void approvedBooking_whenBookingCreated_thenStatusOk() {
        long bookingId = 1L;
        Boolean approved = true;

        BookingInfoDto bookingInfoDto = new BookingInfoDto();

        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
            .thenReturn(bookingInfoDto);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                .header("X-Sharer-User-Id", 1)
                .param("approved", objectMapper.writeValueAsString(approved)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingInfoDto), result);
    }
}