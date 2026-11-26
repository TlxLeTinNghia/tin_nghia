package com.example.tin_nghia.domain;

import java.math.BigDecimal;

public class Product {
    private final int productId;
    private final String productName;
    private final String description;
    private final int manufacturerId;
    private final int categoryId;
    private final boolean hasImage;
    private final int reviewCount;
    private final BigDecimal averageRating;
    public Product(
            int productId ,
            String productName ,
            String description ,
            int manufacturerId ,
            int categoryId,
            boolean hasImage ,
            int reviewCount ,
            BigDecimal averageRating)
    {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.manufacturerId = manufacturerId;
        this.categoryId = categoryId;
        this.hasImage = hasImage;
        this.reviewCount = reviewCount;
        this.averageRating = averageRating;
    }
    public int getProductId(){
        return this.productId;
    }
    public String getProductName(){
        return this.productName;
    }
    public String getDescription(){
        return this.description;
    }

    public int getManufacturerId() {
        return manufacturerId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public boolean getHasImage(){
        return this.hasImage;
    }
    public int getReviewCount(){
        return this.reviewCount;
    }
    public BigDecimal getAverageRating(){
        return this.averageRating;
    }
}

