package ru.practicum.shareit.exception;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleWrongOwner(final Exception e) {
        log.error("error = " + e.getMessage() + ", httpStatus = " + HttpStatus.NOT_FOUND);
        log.error(Arrays.toString(e.getStackTrace()));
        ErrorDto errorDto = new ErrorDto();
        errorDto.setMessage(e.getMessage());
        return errorDto;
    }

    @ExceptionHandler({ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleValidation(final Exception e) {
        log.error("error = " + e.getMessage() + ", httpStatus = " + HttpStatus.BAD_REQUEST);
        log.error(Arrays.toString(e.getStackTrace()));
        ErrorDto errorDto = new ErrorDto();
        errorDto.setMessage(e.getMessage());
        return errorDto;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleException(final Exception e) {
        log.error("error = " + e.getMessage() + ", httpStatus = " + HttpStatus.INTERNAL_SERVER_ERROR);
        log.error(Arrays.toString(e.getStackTrace()));
        ErrorDto errorDto = new ErrorDto();
        errorDto.setMessage(e.getMessage());
        return errorDto;
    }
}