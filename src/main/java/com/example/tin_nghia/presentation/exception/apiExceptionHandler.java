package com.example.tin_nghia.presentation.exception;

import com.example.tin_nghia.application.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.multipart.MaxUploadSizeExceededException;

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
    @ExceptionHandler(UserNameAlreadyExistsException.class)
    public ResponseEntity<Map<String,String>> handlerUserNameAlreadyExistsException(UserNameAlreadyExistsException exception){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message",exception.getMessage()));
    }
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String , String>> handlerOrderNotFoundException(OrderNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message",exception.getMessage()));
    }
    @ExceptionHandler(VariantImagePositionAlreadyUsedException.class)
    public ResponseEntity<?> handlerVariantImagePositionAlreadyUsedException(VariantImagePositionAlreadyUsedException exception){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(ProductVariantNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlerVariantNotFound(ProductVariantNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handlerUploadTooLarge() {
        return ResponseEntity.status(413).body(Map.of("message", "Ảnh phải từ 1 MB trở xuống."));
    }

    @ExceptionHandler(VariantStockChangedException.class)
    public ResponseEntity<Map<String, String>> handlerVariantStockChanged(VariantStockChangedException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", exception.getMessage()));
    }
}
