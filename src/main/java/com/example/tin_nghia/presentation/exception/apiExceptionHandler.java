package com.example.tin_nghia.presentation.exception;

import com.example.tin_nghia.application.exception.InsufficientStockException;
import com.example.tin_nghia.application.exception.ProductNotFoundException;
import com.example.tin_nghia.application.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
// Annotation đăng ký nơi xử lý lỗi chung cho các controller trả về lỗi chứ ko báo là lỗi server nx
public class apiExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String , String>> handlerIllegalArgument(IllegalArgumentException exception){
        return ResponseEntity.badRequest().body(Map.of("message",exception.getMessage()));
    }
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String , String>> handlerProductNotFoundException(ProductNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message",exception.getMessage()));
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String , String>> handlerUserNotFoundException(UserNotFoundException userNotFoundException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", userNotFoundException.getMessage()));
    }
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String , String>> handlerInsufficientStockException(InsufficientStockException exception){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", exception.getMessage()));
    }
}
