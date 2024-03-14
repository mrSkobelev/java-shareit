package ru.practicum.shareit.booking.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(5).truncatedTo(ChronoUnit.SECONDS));
        bookingDto.setStart(LocalDateTime.now().plusSeconds(4).truncatedTo(ChronoUnit.SECONDS));
        bookingDto.setItemId(1);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result)
            .extractingJsonPathStringValue("$.end")
            .isEqualTo(bookingDto.getEnd().truncatedTo(ChronoUnit.SECONDS).toString());

        assertThat(result)
            .extractingJsonPathStringValue("$.start")
            .isEqualTo(bookingDto.getStart().truncatedTo(ChronoUnit.SECONDS).toString());

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
    }
}