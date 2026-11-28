package com.example.tin_nghia.application.dto;

import java.math.BigDecimal;

public class OrderItem {
    private final String productName;
    private final String variantName;
    private final Integer imageId;
    private final BigDecimal unitPrice;
    private final BigDecimal originalAmount;
    private final BigDecimal finalAmount;
    private final int quantity;
    public OrderItem(String productName , String variantName , Integer imageId , BigDecimal unitPrice , BigDecimal originalAmount , BigDecimal finalAmount , int quantity){
        this.productName = productName;
        this.variantName = variantName;
        this.imageId = imageId;
        this.unitPrice = unitPrice;
        this.originalAmount = originalAmount;
        this.finalAmount = finalAmount;
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public String getVariantName() {
        return variantName;
    }

    public Integer getImageId() {
        return imageId;
        // chú ý
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }
}
