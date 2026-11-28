package com.example.tin_nghia.presentation;

import com.example.tin_nghia.application.dto.PageResult;
import com.example.tin_nghia.application.dto.ProductDetailResponse;
import com.example.tin_nghia.application.service.GetProductDetailService;
import com.example.tin_nghia.application.service.GetProductImageService;
import com.example.tin_nghia.application.service.GetProductService;
import com.example.tin_nghia.application.service.GetProductSuggestionService;
import com.example.tin_nghia.domain.Product;
import com.example.tin_nghia.application.dto.ProductImage;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    // Khai báo các kiểu Media thông dụng cho Header
    private static final Set<String> IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );
    // Tiêm các Bean vào Controller
    private final GetProductSuggestionService getProductSuggestionService;
    private final GetProductService getProductService;
    private final GetProductImageService getProductImageService;
    private final GetProductDetailService getProductDetailService;
    public ProductController(GetProductService getProductService,
                             GetProductImageService getProductImageService ,
                             GetProductSuggestionService getProductSuggestionService,
                             GetProductDetailService getProductDetailService
    ) {
        this.getProductService = getProductService;
        this.getProductImageService = getProductImageService;
        this.getProductSuggestionService = getProductSuggestionService;
        this.getProductDetailService = getProductDetailService;
    }

    @GetMapping
    public PageResult<Product> getProduct(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "keyword", defaultValue = "") String keyword
    ) {
        return getProductService.execute(page, size, keyword);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getProductImage(
            @PathVariable("id") int productId
    ) {
        Optional<ProductImage> image = getProductImageService.execute(productId);

        if (image.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ProductImage productImage = image.get();
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .contentType(MediaType.parseMediaType(productImage.getContentType()))
                .body(productImage.getData());
    }
    @GetMapping("/suggestions")
    public List<String> getProductNames(@RequestParam(name = "keyword" , defaultValue = "") String keyword){
        return getProductSuggestionService.execute(keyword);
    }
    @GetMapping("/{id}")
    public ProductDetailResponse getProductDetail(@PathVariable("id") int productId){
        return getProductDetailService.execute(productId);
    }
}