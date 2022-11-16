package com.prototype.splitwise.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ClientException.class)
    public final ResponseEntity<Object> handleClientException(
            ClientException e) {
        log.error("Client Exception encountered :: ", e);
        return new ResponseEntity<>(AppError.from(e), e.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleGenericException(Exception e, WebRequest request) {
        log.error("Exception encountered :: ", e);
        return new ResponseEntity<>(AppError.from(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
