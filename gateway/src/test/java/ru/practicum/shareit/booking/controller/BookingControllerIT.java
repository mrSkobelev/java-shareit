package ru.practicum.shareit.booking.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerIT {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void getBookingById_whenBookingNotFound_thenStatusNotFound() throws Exception {
        long bookingId = 0L;

        when(bookingClient.getBookingById(anyLong(), anyLong()))
            .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isNotFound());
    }

    @Test
    void createBooking_whenFieldNotValid_thenStatusBadRequest() throws Exception {
        long userId = 0L;
        BookItemRequestDto bookItemRequestDto = new BookItemRequestDto();

        when(bookingClient.createBooking(userId, bookItemRequestDto))
            .thenReturn(ResponseEntity.badRequest().build());

        mockMvc.perform(post("/bookings")
            .header("X-Sharer-User-Id", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookItemRequestDto)))
            .andExpect(status().isBadRequest());
    }
}