package com.prototype.splitwise.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ClientException extends RuntimeException {

    private HttpStatus status = HttpStatus.BAD_REQUEST;

    public ClientException() {
        super();
    }

    public ClientException(String message) {
        super(message);
    }

    public ClientException(Throwable cause) {
        super(cause);
    }

    public ClientException(HttpStatus status) {
        super();
        this.status = status;
    }

    public ClientException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public ClientException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}
