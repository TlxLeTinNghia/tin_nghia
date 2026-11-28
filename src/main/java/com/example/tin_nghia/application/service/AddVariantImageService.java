package com.example.tin_nghia.application.service;

import com.example.tin_nghia.application.port.ProductVariantRepository;
import org.springframework.stereotype.Service;

@Service
public class AddVariantImageService {
    private final ProductVariantRepository productVariantRepository;

    public AddVariantImageService(ProductVariantRepository productVariantRepository) {
        this.productVariantRepository = productVariantRepository;
    }

    public int execute(int variantId, byte[] data, int position) {
        if (position < 1 || position > 3) {
            throw new IllegalArgumentException("Vị trí ảnh phải từ 1 đến 3.");
        }
        return productVariantRepository.insertVariantImage(variantId, data, UploadedImage.contentType(data), position);
    }
}
