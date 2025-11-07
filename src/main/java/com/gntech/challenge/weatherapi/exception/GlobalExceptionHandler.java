package com.gntech.challenge.weatherapi.exception;

import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WeatherException.class)
    public ResponseEntity<Map<String, Object>> handleWeatherException(WeatherException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(body, ex.getStatusCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        body.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, Object> body = new HashMap<>();
        String errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getMessage())
                .reduce((msg1, msg2) -> msg1 + ", " + msg2)
                .orElse("Parâmetro inválido");

        body.put("error", errors);
        body.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParams(MissingServletRequestParameterException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "O parâmetro '" + ex.getParameterName() + "' é obrigatório.");
        body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, Object>> handleFeignException(FeignException  ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Erro ao chamar API externa: " + ex.getMessage());
        body.put("status", ex.status());
        body.put("timestamp", LocalDateTime.now());

        HttpStatus status = HttpStatus.resolve(ex.status());
        if (status == null) {
            status = HttpStatus.BAD_GATEWAY;
        }

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Ocorreu um erro inesperado: " + ex.getMessage());
        body.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
