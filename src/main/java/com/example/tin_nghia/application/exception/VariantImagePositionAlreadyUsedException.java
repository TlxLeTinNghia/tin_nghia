package com.example.tin_nghia.application.exception;

public class VariantImagePositionAlreadyUsedException extends RuntimeException{
    public VariantImagePositionAlreadyUsedException(int variantId , int position){
        super("Vị trí "+ position + " biến thể có mã "+variantId + " đã có ảnh.");
    }
}
