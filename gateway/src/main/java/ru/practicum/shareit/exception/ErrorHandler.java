package ru.practicum.shareit.exception;

import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({ValidationException.class, MissingRequestHeaderException.class,
    MethodArgumentNotValidException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleValidation(final Exception e) {
        log.error("error = " + e.getMessage() + ", httpStatus = " + HttpStatus.BAD_REQUEST);
        ErrorDto errorDto = new ErrorDto();
        errorDto.setError(e.getMessage());
        return errorDto;
    }

    @ExceptionHandler({EmptyFieldsException.class, NullPointerException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleEmptyFields(final Exception e) {
        log.error("error = " + e.getMessage() + ", httpStatus = " + HttpStatus.BAD_REQUEST);
        ErrorDto errorDto = new ErrorDto();
        errorDto.setError(e.getMessage());
        return errorDto;
    }

    @ExceptionHandler({IllegalStateException.class, Exception.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleException(final Exception e) {
        log.error("error = " + e.getMessage() + ", httpStatus = " + HttpStatus.INTERNAL_SERVER_ERROR);
        log.error("trace", e);
        ErrorDto errorDto = new ErrorDto();
        errorDto.setError(e.getMessage());
        return errorDto;
    }
}