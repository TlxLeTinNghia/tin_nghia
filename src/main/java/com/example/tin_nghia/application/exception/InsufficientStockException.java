package com.example.tin_nghia.application.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(int variantId , int availableStock){
        super("Biến thế "+variantId +" chỉ còn " + availableStock + " sản phẩm!");
    }
    public InsufficientStockException(int variantId){
        super("Không đủ tồn kho cho biến thể có mã "+ variantId + ".");
    }
}
