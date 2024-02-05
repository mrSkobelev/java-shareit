package ru.practicum.shareit.exception;

public class WrongOwnerException extends RuntimeException {
    public WrongOwnerException(String s) {
        super(s);
    }
}
