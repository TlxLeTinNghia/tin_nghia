package com.example.tin_nghia.infrastructure.config;

import com.example.tin_nghia.application.port.ProductRepository;
import com.example.tin_nghia.application.port.ProductVariantRepository;
import com.example.tin_nghia.application.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    public GetProductService productService(ProductRepository productRepository){
        return new GetProductService(productRepository);
    }
    @Bean
    public GetProductImageService productImageService(ProductRepository productRepository){
        return new GetProductImageService(productRepository);
    }
    @Bean
    public GetProductSuggestionService getProductSuggestionService(ProductRepository productRepository){
        return new GetProductSuggestionService(productRepository);
    }
    @Bean
    public GetProductDetailService getProductDetailService( ProductRepository productRepository , ProductVariantRepository productVariantRepository ){
        return new GetProductDetailService(productRepository , productVariantRepository);
    }
    @Bean
    public GetVariantImagesService getVariantImagesService(ProductVariantRepository productVariantRepository){
        return new GetVariantImagesService(productVariantRepository);
    }
    @Bean
    public GetVariantImageService getVariantImageService(ProductVariantRepository productVariantRepository){
        return new GetVariantImageService(productVariantRepository);
    }

}
