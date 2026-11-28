package com.example.tin_nghia.application.service;

import com.example.tin_nghia.application.dto.CreateProductRequest;
import com.example.tin_nghia.application.port.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateProductService {
    private final ProductRepository products;
    private final CreateVariantService createVariantService;

    public CreateProductService(ProductRepository products, CreateVariantService createVariantService) {
        this.products = products;
        this.createVariantService = createVariantService;
    }

    @Transactional
    public CreatedProduct execute(CreateProductRequest request) {
        String productName = requiredName(request.productName(), "Tên sản phẩm");
        String description = request.description();
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Mô tả sản phẩm không được để trống.");
        }
        if (request.categoryId() == null || request.categoryId() <= 0 ||
                request.manufacturerId() == null || request.manufacturerId() <= 0) {
            throw new IllegalArgumentException("Chọn danh mục và hãng sản xuất.");
        }

        var variant = request.variant();
        if (variant == null) {
            throw new IllegalArgumentException("Sản phẩm cần có biến thể đầu tiên.");
        }
        int productId = products.insert(productName, description.strip(), request.categoryId(), request.manufacturerId());
        int variantId = createVariantService.execute(productId, variant);
        return new CreatedProduct(productId, variantId);
    }

    private static String requiredName(String name, String label) {
        if (name == null || name.isBlank() || name.strip().length() > 200) {
            throw new IllegalArgumentException(label + " phải có từ 1 đến 200 ký tự.");
        }
        return name.strip();
    }

    public record CreatedProduct(int productId, int variantId) {}
}
