package com.example.tin_nghia.presentation;

import com.example.tin_nghia.application.dto.CreateProductRequest;
import com.example.tin_nghia.application.dto.CreateVariantRequest;
import com.example.tin_nghia.application.port.ProductRepository;
import com.example.tin_nghia.application.service.CreateProductService;
import com.example.tin_nghia.application.service.CreateVariantService;
import com.example.tin_nghia.application.exception.UserNotFoundException;
import com.example.tin_nghia.application.service.AddVariantImageService;
import com.example.tin_nghia.application.service.GetUserService;
import com.example.tin_nghia.application.service.SetProductImageService;
import com.example.tin_nghia.application.service.UpdateVariantService;
import com.example.tin_nghia.domain.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AddVariantImageService addVariantImageService;
    private final SetProductImageService setProductImageService;
    private final GetUserService getUserService;
    private final CreateProductService createProductService;
    private final CreateVariantService createVariantService;
    private final UpdateVariantService updateVariantService;
    private final ProductRepository productRepository;

    public AdminController(
            AddVariantImageService addVariantImageService,
            SetProductImageService setProductImageService,
            GetUserService getUserService,
            CreateProductService createProductService,
            CreateVariantService createVariantService,
            UpdateVariantService updateVariantService,
            ProductRepository productRepository
    ) {
        this.addVariantImageService = addVariantImageService;
        this.setProductImageService = setProductImageService;
        this.getUserService = getUserService;
        this.createProductService = createProductService;
        this.createVariantService = createVariantService;
        this.updateVariantService = updateVariantService;
        this.productRepository = productRepository;
    }

    @PostMapping(value = "/variants/{variantId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> insertVariantImage(
            @PathVariable("variantId") int variantId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("position") int position,
            HttpServletRequest request
    ) throws IOException {
        HttpStatus access = checkAdmin(request);
        if (access != HttpStatus.OK) return accessError(access);

        int imageId = addVariantImageService.execute(variantId, file.getBytes(), position);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("imageId", imageId));
    }

    @PutMapping(value = "/products/{productId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> setProductImage(
            @PathVariable("productId") int productId,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request
    ) throws IOException {
        HttpStatus access = checkAdmin(request);
        if (access != HttpStatus.OK) return accessError(access);

        setProductImageService.execute(productId, file.getBytes());
        return ResponseEntity.ok(Map.of("productId", productId));
    }

    @GetMapping("/products/options")
    public ResponseEntity<?> productOptions(HttpServletRequest request) {
        HttpStatus access = checkAdmin(request);
        if (access != HttpStatus.OK) return accessError(access);

        return ResponseEntity.ok(Map.of(
                "categories", productRepository.findCategoryOptions(),
                "manufacturers", productRepository.findManufacturerOptions()
        ));
    }

    @PostMapping(value = "/products", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createProduct(@RequestBody CreateProductRequest product, HttpServletRequest request) {
        HttpStatus access = checkAdmin(request);
        if (access != HttpStatus.OK) return accessError(access);

        return ResponseEntity.status(HttpStatus.CREATED).body(createProductService.execute(product));
    }


    @PostMapping(value = "/products/{productId}/variants", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createVariant(
            @PathVariable("productId") int productId,
            @RequestBody CreateVariantRequest variant,
            HttpServletRequest request
    ) {
        HttpStatus access = checkAdmin(request);
        if (access != HttpStatus.OK) return accessError(access);

        int variantId = createVariantService.execute(productId, variant);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("productId", productId, "variantId", variantId));
    }


    @GetMapping("/products/{productId}/variants/{variantId}")
    public ResponseEntity<?> variantForEdit(
            @PathVariable("productId") int productId,
            @PathVariable("variantId") int variantId,
            HttpServletRequest request
    ) {
        HttpStatus access = checkAdmin(request);
        if (access != HttpStatus.OK) return accessError(access);

        return ResponseEntity.ok(updateVariantService.find(productId, variantId));
    }

    @PutMapping(value = "/products/{productId}/variants/{variantId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateVariant(
            @PathVariable("productId") int productId,
            @PathVariable("variantId") int variantId,
            @RequestBody UpdateVariantRequest update,
            HttpServletRequest request
    ) {
        HttpStatus access = checkAdmin(request);
        if (access != HttpStatus.OK) return accessError(access);

        updateVariantService.execute(productId, variantId, update.variant(), update.expectedUnitsInStock());
        return ResponseEntity.noContent().build();
    }

    public record UpdateVariantRequest(CreateVariantRequest variant, Integer expectedUnitsInStock) {}


    private HttpStatus checkAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || !(session.getAttribute("userId") instanceof Integer userId)) {
            return HttpStatus.UNAUTHORIZED;
        }
        try {
            return getUserService.execute(userId).getRole() == Role.ADMIN ? HttpStatus.OK : HttpStatus.FORBIDDEN;
        } catch (UserNotFoundException ex) {
            session.invalidate();
            return HttpStatus.UNAUTHORIZED;
        }
    }

    private ResponseEntity<Map<String, String>> accessError(HttpStatus status) {
        String message = status == HttpStatus.FORBIDDEN
                ? "Bạn không có quyền quản lý sản phẩm."
                : "Vui lòng đăng nhập bằng tài khoản quản trị.";
        return ResponseEntity.status(status).body(Map.of("message", message));
    }
}
