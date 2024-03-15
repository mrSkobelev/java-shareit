package ru.practicum.shareit.user.dto;

import javax.validation.constraints.Email;
import lombok.Data;

@Data
public class UserDto {
    private long id;
    private String name;
    @Email(message = "Невалидный email.")
    private String email;
}
