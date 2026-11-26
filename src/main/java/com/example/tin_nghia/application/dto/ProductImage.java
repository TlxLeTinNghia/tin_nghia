package com.example.tin_nghia.application.dto;

public class ProductImage {
    private final byte[] data ;
    private final String contentType;
    public ProductImage(byte[] data , String contentType){
        this.data = data;
        this.contentType = contentType;
    }
    public String getContentType() {
        return contentType;
    }
    public byte[] getData() {
        return data;
    }
}
