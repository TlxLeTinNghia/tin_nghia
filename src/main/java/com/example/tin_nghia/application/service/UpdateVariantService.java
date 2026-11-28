package com.example.tin_nghia.application.service;

import com.example.tin_nghia.application.dto.CreateVariantRequest;
import com.example.tin_nghia.application.exception.ProductVariantNotFoundException;
import com.example.tin_nghia.application.exception.VariantStockChangedException;
import com.example.tin_nghia.application.port.ProductVariantRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateVariantService {
    private final ProductVariantRepository variants;

    public UpdateVariantService(ProductVariantRepository variants) {
        this.variants = variants;
    }

    public VariantForEdit find(int productId, int variantId) {
        var variant = variants.findById(variantId)
                .filter(value -> value.getProductId() == productId)
                .orElseThrow(() -> new ProductVariantNotFoundException(variantId));
        return new VariantForEdit(
                variant.getVariantId(), variant.getVariantName(), variant.getUnitPrice().stripTrailingZeros().toPlainString(),
                variant.getUnitsInStock(), variant.getDiscount().movePointRight(2).stripTrailingZeros().toPlainString()
        );
    }

    public void execute(int productId, int variantId, CreateVariantRequest request, Integer expectedUnitsInStock) {
        if (expectedUnitsInStock == null || expectedUnitsInStock < 0) {
            throw new IllegalArgumentException("Cần tải tồn kho hiện tại trước khi lưu.");
        }
        var variant = CreateVariantService.validate(request);
        boolean updated = variants.update(
                productId, variantId, variant.variantName(), variant.unitPrice(),
                variant.unitsInStock(), variant.discountPercent().movePointLeft(2), expectedUnitsInStock
        );
        if (!updated) {
            find(productId, variantId);
            throw new VariantStockChangedException();
        }
    }

    public record VariantForEdit(int variantId, String variantName, String unitPrice,
                                 int unitsInStock, String discountPercent) {}
}
