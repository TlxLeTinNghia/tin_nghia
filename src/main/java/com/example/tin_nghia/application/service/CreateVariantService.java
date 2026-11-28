package com.example.tin_nghia.application.service;

import com.example.tin_nghia.application.dto.CreateVariantRequest;
import com.example.tin_nghia.application.port.ProductVariantRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CreateVariantService {
    private static final BigDecimal MAX_UNIT_PRICE = new BigDecimal("999999999999999.999");
    private static final BigDecimal MAX_DISCOUNT_PERCENT = new BigDecimal("100");

    private final ProductVariantRepository variants;

    public CreateVariantService(ProductVariantRepository variants) {
        this.variants = variants;
    }

    public int execute(int productId, CreateVariantRequest request) {
        var variant = validate(request);
        return variants.insert(productId, variant.variantName(), variant.unitPrice(), variant.unitsInStock(), variant.discountPercent().movePointLeft(2));
    }

    static CreateVariantRequest validate(CreateVariantRequest variant) {
        if (variant == null) {
            throw new IllegalArgumentException("Cần nhập thông tin biến thể.");
        }
        String name = variant.variantName();
        if (name == null || name.isBlank() || name.strip().length() > 200) {
            throw new IllegalArgumentException("Tên biến thể phải có từ 1 đến 200 ký tự.");
        }
        if (!validDecimal(variant.unitPrice(), MAX_UNIT_PRICE, 3)) {
            throw new IllegalArgumentException("Đơn giá phải từ 0 trở lên và có tối đa 3 chữ số thập phân.");
        }
        if (variant.unitsInStock() == null || variant.unitsInStock() < 0) {
            throw new IllegalArgumentException("Tồn kho phải là số nguyên từ 0 trở lên.");
        }
        BigDecimal percent = variant.discountPercent() == null ? BigDecimal.ZERO : variant.discountPercent();
        if (!validDecimal(percent, MAX_DISCOUNT_PERCENT, 2)) {
            throw new IllegalArgumentException("Giảm giá phải từ 0 đến 100% và có tối đa 2 chữ số thập phân.");
        }

        return new CreateVariantRequest(name.strip(), variant.unitPrice(), variant.unitsInStock(), percent);
    }

    private static boolean validDecimal(BigDecimal value, BigDecimal max, int scale) {
        return value != null && value.signum() >= 0 && value.compareTo(max) <= 0 &&
                value.stripTrailingZeros().scale() <= scale;
    }
}
