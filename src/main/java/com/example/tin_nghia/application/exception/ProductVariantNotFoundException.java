package com.example.tin_nghia.application.exception;

public class ProductVariantNotFoundException extends RuntimeException {
    public ProductVariantNotFoundException(int variantId){
        super("Không tìm thấy biến thể sản phẩm có mã : " + variantId);
    }

}
