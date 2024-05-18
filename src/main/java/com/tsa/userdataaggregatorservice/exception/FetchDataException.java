package com.tsa.userdataaggregatorservice.exception;

public class FetchDataException extends RuntimeException {
    public FetchDataException(String formatted, Throwable e) {
        super(formatted, e);
    }
}
