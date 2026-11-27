package com.example.tin_nghia.application.exception;

public class UserNameAlreadyExistsException extends RuntimeException{
    public UserNameAlreadyExistsException(){
        super("Tên đăng nhập đã được sử dụng!");
    }
}
