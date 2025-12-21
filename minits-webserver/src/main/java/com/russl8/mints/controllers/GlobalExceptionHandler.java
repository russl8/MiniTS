package com.russl8.mints.controllers;

import exception.SyntaxException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SyntaxException.class)
    public ResponseEntity<?> handleSyntax(SyntaxException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        "success", false,
                        "type", "SYNTAX_ERROR",
                        "errors", e.getErrors()
                )
        );
    }
}
