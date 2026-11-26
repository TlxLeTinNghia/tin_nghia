package com.example.tin_nghia.infrastructure.persistence;

import com.example.tin_nghia.application.dto.VariantImage;
import com.example.tin_nghia.application.dto.VariantImages;
import com.example.tin_nghia.application.port.ProductVariantRepository;
import com.example.tin_nghia.domain.ProductVariant;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public class JdbcProductVariantRepository implements ProductVariantRepository {
    private final JdbcTemplate jdbcTemplate;
    public JdbcProductVariantRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<ProductVariant> findVariantsByProductId(int productId){
        String sql = """
                select VariantID, VariantName, ProductID, UnitPrice, UnitsInStock, Discount
                from ProductVariants
                where ProductID = ?
                order by VariantID;""";
        return jdbcTemplate.query(sql,(rs, rowNum) -> new ProductVariant(
                rs.getInt("VariantId"),
                rs.getString("VariantName"),
                rs.getInt("ProductId"),
                rs.getBigDecimal("UnitPrice"),
                rs.getInt("UnitsInStock"),
                rs.getBigDecimal("Discount")
        ),productId);
    }
    @Override
    public List<VariantImages> findImagesByVariantId(int variantId){
        String sql = """
                select ImageId , Position
                from VariantImages
                where VariantID = ?
                Order by Position asc;
                """;
        return jdbcTemplate.query(sql, (rs , rowNum)-> new VariantImages(rs.getInt("ImageId")),variantId);
    }
    @Override
    public Optional<VariantImage> findImageByImageId(int imageId){
        String sql = """
                select ImageData , ImageType
                from VariantImages
                where ImageID = ?;
                """;
        return jdbcTemplate.query(sql , (rs , rowNum)-> new VariantImage(
                rs.getBytes("ImageData"),
                rs.getString("ImageType")
        ),imageId).stream().findFirst();
    }
    @Override
    public List<Integer> findImageIdsByProductId(int productId){
        String sql = """
                select VI.ImageID
                from ProductVariants as PV
                inner join VariantImages as VI on PV.VariantID = VI.VariantID
                where PV.ProductID = ?
                order by VI.VariantID , VI.Position;
                """;
        return jdbcTemplate.query(sql ,  (rs, rowNum) -> rs.getInt("ImageID"),productId);
    }
    @Override
    public Optional<ProductVariant> findById(int variantId){
        String sql = """
                select VariantID , VariantName , ProductID , UnitPrice , UnitsInStock , Discount
                from ProductVariants
                where VariantID = ?;
                """;
        return jdbcTemplate.query(sql,(rs,rowNum) -> new ProductVariant(
                rs.getInt("VariantID"),
                rs.getString("VariantName"),
                rs.getInt("ProductID"),
                rs.getBigDecimal("UnitPrice"),
                rs.getInt("UnitsInStock"),
                rs.getBigDecimal("Discount")
        ),variantId).stream().findFirst();
    }
}
