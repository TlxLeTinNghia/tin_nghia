package com.example.tin_nghia.application.service;

import com.example.tin_nghia.application.dto.ProductDetailResponse;
import com.example.tin_nghia.application.exception.ProductNotFoundException;
import com.example.tin_nghia.application.port.ProductRepository;
import com.example.tin_nghia.application.port.ProductVariantRepository;
import com.example.tin_nghia.domain.Product;
import com.example.tin_nghia.domain.ProductVariant;

import java.util.List;
import java.util.Optional;

public class GetProductDetailService {
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    public GetProductDetailService(ProductRepository productRepository , ProductVariantRepository productVariantRepository){
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
    }
    public ProductDetailResponse execute(int productId){
        Product p1 = productRepository.findById(productId).orElseThrow(()->new ProductNotFoundException(productId));
        List<ProductVariant> productVariants = productVariantRepository.findVariantsByProductId(productId);
        List<Integer> imageIds = productVariantRepository.findImageIdsByProductId(productId);
        return new ProductDetailResponse(
                p1.getProductId(),
                p1.getProductName(),
                p1.getDescription(),
                p1.getManufacturerId(),
                p1.getCategoryId(),
                p1.getHasImage(),
                p1.getReviewCount(),
                p1.getAverageRating(),
                productVariants,
                imageIds
        );
    }
}
