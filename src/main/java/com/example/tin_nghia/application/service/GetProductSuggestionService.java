package com.example.tin_nghia.application.service;

import com.example.tin_nghia.application.port.ProductRepository;

import java.util.List;

public class GetProductSuggestionService {
    private final ProductRepository productRepository;
    public GetProductSuggestionService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public List<String> execute(String keyword){
        String normalizeKeyword = keyword ==null?"":keyword.trim();
        if(normalizeKeyword.isEmpty())
            return List.of();
        return productRepository.findProductNamesByKeyword(normalizeKeyword);
    }
}
