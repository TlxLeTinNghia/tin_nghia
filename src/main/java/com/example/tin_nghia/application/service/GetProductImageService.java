package com.example.tin_nghia.application.service;

import com.example.tin_nghia.application.port.ProductRepository;
import com.example.tin_nghia.application.dto.ProductImage;

import java.util.Optional;

public class GetProductImageService {
    private final ProductRepository productRepository;
    public GetProductImageService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }
    public Optional<ProductImage> execute(int productId){
        return productRepository.findProductImageByproductId(productId);
    }
}
