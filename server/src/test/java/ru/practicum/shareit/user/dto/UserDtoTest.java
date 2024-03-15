package ru.practicum.shareit.user.dto;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

@JsonTest
class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @SneakyThrows
    @Test
    void testUserDto() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@test.ru");
        userDto.setName("test");
        userDto.setId(1);

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("test@test.ru");
    }
}