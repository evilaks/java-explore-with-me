package ru.practicum.exception;

import lombok.Getter;

@Getter
public class StatsClientException extends RuntimeException {

    private final String reason;

    public StatsClientException(String reason, String message) {
        super(message);
        this.reason = reason;
    }

}
