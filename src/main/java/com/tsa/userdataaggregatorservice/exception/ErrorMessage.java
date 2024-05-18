package com.tsa.userdataaggregatorservice.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
public class ErrorMessage {
    private HttpStatus status;
    private String message;
}
