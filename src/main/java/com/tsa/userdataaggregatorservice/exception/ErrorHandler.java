package com.tsa.userdataaggregatorservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(FetchDataException.class)
    public ResponseEntity<ErrorMessage> genreNotFoundException(FetchDataException e) {
        return new ResponseEntity<>(ErrorMessage.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(e.getMessage())
                .build(),
                HttpStatus.BAD_REQUEST);
    }
}
