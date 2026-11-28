package com.example.tin_nghia.application.port;

import com.example.tin_nghia.application.dto.VariantImage;
import com.example.tin_nghia.application.dto.VariantImages;
import com.example.tin_nghia.domain.ProductVariant;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository {
    List<ProductVariant> findVariantsByProductId(int productId);
    List<VariantImages> findImagesByVariantId(int variantId);
    Optional<VariantImage> findImageByImageId(int imageId);
    List<Integer> findImageIdsByProductId(int productId);
    Optional<ProductVariant> findById(int variantId);
    int insertVariantImage(int variantId , byte[] data , String imageType , int position);

    int insert(int productId, String variantName, BigDecimal unitPrice, int unitsInStock, BigDecimal discount);

    boolean update(int productId, int variantId, String variantName, BigDecimal unitPrice,
                   int unitsInStock, BigDecimal discount, int expectedUnitsInStock);
}
