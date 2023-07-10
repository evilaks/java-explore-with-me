package ru.practicum.exception;

import lombok.Getter;

@Getter
public class ConflictRequestException extends RuntimeException {

    private final String reason;

    public ConflictRequestException(String reason, String message) {
        super(message);
        this.reason = reason;
    }
}