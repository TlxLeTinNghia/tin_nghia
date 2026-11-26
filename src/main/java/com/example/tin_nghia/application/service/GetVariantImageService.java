package com.example.tin_nghia.application.service;

import com.example.tin_nghia.application.dto.VariantImage;
import com.example.tin_nghia.application.port.ProductVariantRepository;

import java.util.Optional;

public class GetVariantImageService {
    private final ProductVariantRepository productVariantRepository;
    public GetVariantImageService(ProductVariantRepository productVariantRepository){
        this.productVariantRepository = productVariantRepository;
    }
    public Optional<VariantImage> excute(int imageId){
        return productVariantRepository.findImageByImageId(imageId);
    }
}
