package com.prototype.splitwise.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Getter
public class AppError {

    private final HttpStatus status;
    private final Instant timestamp;
    private final String error;


    private AppError(HttpStatus status, String error) {
        this.status = status;
        this.error = error;
        this.timestamp = Instant.now();
    }

    public static AppError from(String error) {
        return new AppError(HttpStatus.INTERNAL_SERVER_ERROR, error);
    }

    public static AppError from(ClientException e) {
        return new AppError(e.getStatus(), e.getMessage());
    }
}
