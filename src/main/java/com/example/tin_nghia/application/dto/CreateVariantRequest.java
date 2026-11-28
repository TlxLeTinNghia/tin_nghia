package com.example.tin_nghia.application.dto;

import java.math.BigDecimal;

public record CreateVariantRequest(
        String variantName,
        BigDecimal unitPrice,
        Integer unitsInStock,
        BigDecimal discountPercent
) {}
