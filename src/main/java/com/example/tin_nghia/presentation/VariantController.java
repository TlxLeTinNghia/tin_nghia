package com.example.tin_nghia.presentation;

import com.example.tin_nghia.application.dto.VariantImage;
import com.example.tin_nghia.application.dto.VariantImages;
import com.example.tin_nghia.application.service.GetVariantImageService;
import com.example.tin_nghia.application.service.GetVariantImagesService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/variants")
public class VariantController {
    private final GetVariantImagesService getVariantImagesService;
    private final GetVariantImageService getVariantImageService;
    public VariantController(
            GetVariantImagesService getVariantImagesService,
            GetVariantImageService getVariantImageService
            ){
        this.getVariantImagesService = getVariantImagesService;
        this.getVariantImageService = getVariantImageService;
    }
    @GetMapping("/{id}/images")
    public List<VariantImages> getImages(@PathVariable("id") int variantId){
        return getVariantImagesService.execute(variantId);
    }
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") int imageId) {
        Optional<VariantImage> image = getVariantImageService.excute(imageId);
//        if(image.isEmpty()){
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(image.get().getContentType()))
//                .body(image.get().getData());

        return image.map(img -> ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(img.getContentType()))
                .body(img.getData())).orElseGet(() -> ResponseEntity.notFound().build());
        // Trên là cách xử lý khác cho Optional<> bằng map

    }
}
