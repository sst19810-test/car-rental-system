package com.carrentalsystem.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex, WebRequest request) {
        ex.printStackTrace(); // Log the exception for debugging
        System.out.println(ex.getMessage());
        return new ResponseEntity<>("An error occurred:   " + ex.getMessage() , HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
