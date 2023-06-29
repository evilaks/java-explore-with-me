package ru.practicum.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

    private final String reason;

    public BadRequestException(String reason, String message) {
        super(message);
        this.reason = reason;
    }
}
