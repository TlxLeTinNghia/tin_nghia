package com.example.tin_nghia.application.exception;

public class OrderNotFoundException extends RuntimeException{
    public OrderNotFoundException(int orderId){
        super("Không tìm thấy đơn có mã "+orderId);
    }
}
