package com.example.tin_nghia.application.service;

import com.example.tin_nghia.application.exception.ProductNotFoundException;
import com.example.tin_nghia.application.port.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class SetProductImageService {
    private final ProductRepository productRepository;

    public SetProductImageService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void execute(int productId, byte[] data) {
        String imageType = UploadedImage.contentType(data);
        if (!productRepository.updateImage(productId, data, imageType)) {
            throw new ProductNotFoundException(productId);
        }
    }
}
