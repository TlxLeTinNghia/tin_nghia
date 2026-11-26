package com.example.tin_nghia.application.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(int id){
        super("Không tìm thấy sản phẩm có mã : " + id);
        // gọi hàm dựng của cha hàm này , tham số truyền vào là tham số của hàm dựng của cha nó
    }
}
// RunTimeException có hàm getMessage()