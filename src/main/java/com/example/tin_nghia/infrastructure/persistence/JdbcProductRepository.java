package com.example.tin_nghia.infrastructure.persistence;

import com.example.tin_nghia.application.port.ProductRepository;
import com.example.tin_nghia.domain.Product;
import com.example.tin_nghia.application.dto.ProductImage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcProductRepository implements ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Product> findPage(int offset, int limit, String keyword) {
        String sql = """
                Select p.ProductID,
                	p.ProductName,
                	p.[Description],
                	p.ManufacturerID,
                	p.CategoryID,
                	CASE WHEN p.ImageData is null THEN 0 else 1 END as HasImage,
                	COALESCE(r.ReviewCount , 0) as ReviewCount,
                	r.AverageRating
                From Products as p
                Left join (
                	Select ProductID ,
                		COUNT(*) as ReviewCount,
                		AVG(Cast(Star as decimal(4,2))) as AverageRating
                	From Reviews
                	group by ProductID
                ) as r on  p.ProductID = r.ProductID
                Where P.ProductName LIKE ?
                Order by p.ProductID
                OFFSET ? ROWS
                FETCH NEXT ? ROWS ONLY;
                """;
        String _keyword = '%'+keyword+'%';
        return jdbcTemplate.query(sql , (rs , rowNum)-> new Product(
            rs.getInt("ProductID"),
            rs.getString("ProductName"),
            rs.getString("Description"),
            rs.getInt("ManufacturerID"),
            rs.getInt("CategoryID"),
            rs.getBoolean("HasImage"),
            rs.getInt("ReviewCount"),
            rs.getBigDecimal("AverageRating")
        ),_keyword , offset , limit);
    }

    @Override
    public long countByName(String keyword) {
        String sql = """
                select COUNT_BIG(*) as [Quantity]
                from Products
                where ProductName LIKE ?;
                """;
        String searchPattern = '%'+keyword+'%';
        Long total = jdbcTemplate.queryForObject(sql , Long.class ,searchPattern);
        return total;
    }
    @Override
    public Optional<ProductImage> findProductImageByproductId(int productId){
        String sql = """
                select ImageData , ImageType
                from Products
                where productId = ? and ImageData is not null;
                """;
        return jdbcTemplate.query(sql,(rs , rowNum)->new ProductImage(
                rs.getBytes("ImageData"),
                rs.getString("ImageType")
                ),productId
        ).stream().findFirst();
    }
    @Override
    public List<String> findProductNamesByKeyword(String keyword){
        String sql = """
                select top(5) ProductName
                from Products
                where ProductName LIKE ?
                order by ProductName , ProductId;
                """;
        return jdbcTemplate.query(sql , (rs, rowNum) -> rs.getString("ProductName")
                ,'%'+keyword+'%');
        // cái getString() là trả về kiểu dữ liệu String
    }
    @Override
    public Optional<Product> findById(int productID){
        String sql = """
                
                select p.ProductID,
                    p.ProductName,
                    p.[Description],
                    p.ManufacturerID,
                    p.CategoryID,
                    CASE WHEN p.ImageData is null Then 0 else 1 END as HasImage,
                    COALESCE(r.ReviewCount , 0) as ReviewCount,
                    r.AverageRating
                from Products as p
                left join (
                    select ProductId,
                        Count(*) as ReviewCount,
                        AVG(CAST(Star as decimal(4,2))) as AverageRating
                    from Reviews
                    group by ProductID
                ) as r on p.ProductID = r.ProductID
                where p.ProductID = ?;
                """;
        return jdbcTemplate.query(sql , (rs , rowNum)->new Product(
                rs.getInt("ProductID"),
                rs.getString("ProductName"),
                rs.getString("Description"),
                rs.getInt("ManufacturerID"),
                rs.getInt("CategoryID"),
                rs.getBoolean("HasImage"),
                rs.getInt("ReviewCount"),
                rs.getBigDecimal("AverageRating")
        ),productID).stream().findFirst();
    }
}