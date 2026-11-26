package com.example.tin_nghia.application.service;

import com.example.tin_nghia.application.dto.VariantImages;
import com.example.tin_nghia.application.port.ProductVariantRepository;

import java.util.List;

public class GetVariantImagesService {
    private final ProductVariantRepository productVariantRepository;
    public GetVariantImagesService(ProductVariantRepository productVariantRepository){
        this.productVariantRepository = productVariantRepository;
    }
    public List<VariantImages> execute(int variantId){
        return productVariantRepository.findImagesByVariantId(variantId);
    }
}
