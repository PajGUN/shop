package ru.sunlab.shop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.sunlab.shop.exception.*;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@ControllerAdvice
public class AdviceController extends ResponseEntityExceptionHandler {

    private static final String TIMESTAMP = "timestamp";
    private static final String STATUS = "status";
    private static final String FIELD = "field";
    private static final String MESSAGE = "message";
    private static final String WRONG_VALUE = "wrong value";
    private static final String ERROR = "error";

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error(ex.toString());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(STATUS, status.value());

        body.put(MESSAGE, ex.getMessage());
        body.put(ERROR, "validation error");
        return new ResponseEntity<>(body, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error(ex.toString());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(STATUS, status.value());

        List<Map<String, Object>> errors = new ArrayList<>();
        for(FieldError fieldError : ex.getBindingResult().getFieldErrors()){
            Map<String, Object> map = new HashMap<>();
            map.put(FIELD, fieldError.getField());
            map.put(MESSAGE, fieldError.getDefaultMessage());
            map.put(WRONG_VALUE, fieldError.getRejectedValue());
            errors.add(map);
        }
        body.put(ERROR, errors);
        body.put(MESSAGE, "validation error");
        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler({ProductCountNotFoundException.class, ProductNotFoundException.class,
                       StoreNotFoundException.class, ProductTypeException.class, RabbitException.class})
    protected ResponseEntity<Object> handleEntityNotFoundException(RuntimeException ex){
        log.error(ex.toString());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(STATUS, HttpStatus.NOT_FOUND.value());
        body.put(MESSAGE, ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}
