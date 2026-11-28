package com.example.tin_nghia.application.exception;

public class VariantStockChangedException extends RuntimeException {
    public VariantStockChangedException() {
        super("Tồn kho đã thay đổi. Hãy tải lại biến thể và kiểm tra số lượng trước khi lưu.");
    }
}
