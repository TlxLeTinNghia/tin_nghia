package com.example.tin_nghia.infrastructure.persistence;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcCategoryRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcCategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CategorySummary> findPage(long offset, int limit) {
        String sql = """
                SELECT CategoryID, CategoryName,
                       CASE WHEN DATALENGTH(ImageData) > 0 THEN 1 ELSE 0 END AS HasImage
                FROM dbo.Categories
                ORDER BY CategoryID
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new CategorySummary(
                rs.getInt("CategoryID"), rs.getString("CategoryName"), rs.getBoolean("HasImage")
        ), offset, limit);
    }

    public Optional<CategoryImage> findImage(int categoryId) {
        String sql = """
                SELECT ImageData, ImageType
                FROM dbo.Categories
                WHERE CategoryID = ? AND DATALENGTH(ImageData) > 0;
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new CategoryImage(
                rs.getBytes("ImageData"), rs.getString("ImageType")
        ), categoryId).stream().findFirst();
    }

    public static final class CategorySummary {
        private final int categoryId;
        private final String categoryName;
        private final boolean hasImage;

        public CategorySummary(int categoryId, String categoryName, boolean hasImage) {
            this.categoryId = categoryId;
            this.categoryName = categoryName;
            this.hasImage = hasImage;
        }

        public int getCategoryId() { return categoryId; }
        public String getCategoryName() { return categoryName; }
        public boolean getHasImage() { return hasImage; }
    }

    public static final class CategoryImage {
        private final byte[] data;
        private final String contentType;

        public CategoryImage(byte[] data, String contentType) {
            this.data = data;
            this.contentType = contentType;
        }

        public byte[] getData() { return data; }
        public String getContentType() { return contentType; }
    }
}
