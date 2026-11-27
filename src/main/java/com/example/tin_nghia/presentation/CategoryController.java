package com.example.tin_nghia.presentation;

import com.example.tin_nghia.infrastructure.persistence.JdbcCategoryRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private static final int PAGE_SIZE = 12;
    private static final Set<MediaType> IMAGE_TYPES = Set.of(
            MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG, MediaType.IMAGE_GIF,
            MediaType.valueOf("image/webp"), MediaType.valueOf("image/avif")
    );

    private final JdbcCategoryRepository categoryRepository;

    public CategoryController(JdbcCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCategories(@RequestParam(name = "page", defaultValue = "0") int page) {
        if (page < 0) {
            return ResponseEntity.badRequest().build();
        }
        var rows = categoryRepository.findPage((long) page * PAGE_SIZE, PAGE_SIZE + 1);
        return ResponseEntity.ok(Map.of(
                "content", rows.subList(0, Math.min(PAGE_SIZE, rows.size())),
                "hasNext", rows.size() > PAGE_SIZE
        ));
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") int categoryId) {
        var result = categoryRepository.findImage(categoryId);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var image = result.get();
        try {
            MediaType type = MediaType.parseMediaType(image.getContentType());
            if (IMAGE_TYPES.contains(type)) {
                return ResponseEntity.ok().contentType(type).body(image.getData());
            }
        } catch (IllegalArgumentException ignored) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.notFound().build();
    }
}
