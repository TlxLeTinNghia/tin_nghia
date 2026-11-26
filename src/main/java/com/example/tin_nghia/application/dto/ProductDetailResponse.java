package com.example.tin_nghia.application.dto;

import com.example.tin_nghia.domain.ProductVariant;

import java.math.BigDecimal;
import java.util.List;

public class ProductDetailResponse {
    private final int productId;
    private final String productName;
    private final String description;
    private final int manufacturerId;
    private final int categoryId;
    private final boolean hasImage;
    private final int reviewCount;
    private final BigDecimal averageRating;
    private final List<ProductVariant> variants;
    private final List<Integer> imageIds;
    public ProductDetailResponse(
            int productId ,
            String productName ,
            String description ,
            int manufacturerId ,
            int categoryId ,
            boolean hasImage,
            int reviewCount ,
            BigDecimal averageRating,
            List<ProductVariant> variants,
            List<Integer> imageIds
            ){
        this.productId = productId;
        this.description = description;
        this.productName = productName;
        this.manufacturerId = manufacturerId;
        this.categoryId = categoryId;
        this.hasImage = hasImage;
        this.reviewCount = reviewCount;
        this.averageRating = averageRating;
        this.variants = variants;
        this.imageIds = imageIds;
    }

    public int getProductId() {
        return productId;
    }
    public String getProductName() {
        return productName;
    }

    public String getDescription() {
        return description;
    }

    public int getManufacturerId() {
        return manufacturerId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public List<ProductVariant> getVariants() {
        return variants;
    }

    public List<Integer> getImageIds() {
        return imageIds;
    }
}
