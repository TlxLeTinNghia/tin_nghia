package com.example.tin_nghia.domain;

import java.math.BigDecimal;

public class ProductVariant {
    private final int variantId;
    private final  String variantName;
    private final int productId;
    private final BigDecimal unitPrice;
    private final int unitsInStock;
    private final BigDecimal discount;
    public ProductVariant(
            int variantId ,
            String variantName ,
            int productId ,
            BigDecimal unitPrice ,
            int unitsInStock ,
            BigDecimal discount ){
        this.variantId = variantId;
        this.variantName = variantName;
        this.productId = productId;
        this.unitPrice = unitPrice;
        this.unitsInStock = unitsInStock;
        this.discount = discount;
    }

    public int getVariantId() {
        return variantId;
    }

    public String getVariantName() {
        return variantName;
    }

    public int getProductId() {
        return productId;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getUnitsInStock() {
        return unitsInStock;
    }

    public BigDecimal getDiscount() {
        return discount;
    }
}
