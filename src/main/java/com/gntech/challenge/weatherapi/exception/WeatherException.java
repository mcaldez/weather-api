package com.gntech.challenge.weatherapi.exception;

import org.springframework.http.HttpStatus;

public class WeatherException extends RuntimeException {

    private final HttpStatus status;

    public WeatherException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatusCode() {
        return status;
    }
}