package com.example.tin_nghia.application.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(int userId , Throwable cause){
        super("Không tìm thấy người mua có mã "+userId +"."+cause);
    }
}
