package com.example.tin_nghia.application.port;

import com.example.tin_nghia.application.dto.CatalogOption;
import com.example.tin_nghia.domain.Product;
import com.example.tin_nghia.application.dto.ProductImage;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findPage(int offset , int limit , String keyword);
    long countByName(String keyword);
    Optional<ProductImage> findProductImageByproductId(int productId);
    List<String> findProductNamesByKeyword(String keyword);
    Optional<Product> findById(int productId);
    boolean updateImage(int productId, byte[] data, String imageType);

    int insert(String name, String description, int categoryId, int manufacturerId);
    List<CatalogOption> findCategoryOptions();
    List<CatalogOption> findManufacturerOptions();
}
