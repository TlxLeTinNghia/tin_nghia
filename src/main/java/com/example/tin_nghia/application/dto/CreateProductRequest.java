package com.example.tin_nghia.application.dto;

public record CreateProductRequest(
        String productName,
        String description,
        Integer categoryId,
        Integer manufacturerId,
        CreateVariantRequest variant
) {}
