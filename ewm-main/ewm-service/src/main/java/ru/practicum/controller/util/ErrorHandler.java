package ru.practicum.controller.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.ErrorResponse;
import org.postgresql.util.PSQLException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final BadRequestException e) {
        log.warn("BadRequest exception caught with message: {}", e.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST, e.getReason(), e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(PSQLException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handlePSQLException(final PSQLException e) {
        log.warn("PSQL exception caught with message: {}", e.getMessage());
        return new ErrorResponse(HttpStatus.CONFLICT, "Error running SQL-script", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.warn("NotFound exception caught with message: {}", e.getMessage());
        return new ErrorResponse(HttpStatus.NOT_FOUND, e.getReason(), e.getMessage(), LocalDateTime.now());
    }
}
