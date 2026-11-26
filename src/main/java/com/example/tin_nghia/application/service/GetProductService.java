package com.example.tin_nghia.application.service;

import com.example.tin_nghia.application.dto.PageResult;
import com.example.tin_nghia.application.port.ProductRepository;
import com.example.tin_nghia.domain.Product;

import java.util.List;

public class GetProductService {
    private final ProductRepository productRepository;
    public GetProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }
    public PageResult<Product> execute(int page , int size , String keyword){
        String normalizedKeyword = keyword == null ? "" : keyword.trim(); // hàm trim() là bỏ đi khoảng trắng đầu + cuối
        if(page<0){
            throw new IllegalArgumentException(
                    "Số trang phải lớn hơn hoặc bằng 0 !"
            );
        }

        if(size < 1 || size >100 ){
            throw new IllegalArgumentException(
                    "Số sản phẩm mỗi trang phải từ 1 cho đến 100 !"
            );
        }

        long calculatedOffset = (long)page*size;

        if( calculatedOffset > Integer.MAX_VALUE){
            throw new IllegalArgumentException("Số trang vượt quá giới hạn hỗ trợ !"
            );
        }

        int offset = (int)calculatedOffset;

        List<Product> products = productRepository.findPage(offset , size , normalizedKeyword);

        long totalElements = productRepository.countByName(normalizedKeyword);

        return new PageResult<Product>(
                products,
                page,
                size,
                totalElements
        );

    }
}
