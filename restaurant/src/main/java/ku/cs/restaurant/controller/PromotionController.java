package ku.cs.restaurant.controller;

import ku.cs.restaurant.dto.ApiResponse;
import ku.cs.restaurant.dto.promotion.PromotionCreateRequest;
import ku.cs.restaurant.entity.Promotion;
import ku.cs.restaurant.service.ImageService;
import ku.cs.restaurant.service.PromotionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class PromotionController {
    private final PromotionService promotionService;
    private final ImageService imageService;

    public PromotionController(PromotionService promotionService, ImageService imageService) {
        this.promotionService = promotionService;
        this.imageService = imageService;
    }

    // Create a new promotion
    @PostMapping("/promotions")
    public ResponseEntity<ApiResponse<Promotion>> createPromotion(@RequestPart("promotion") PromotionCreateRequest request,
                                                                  @RequestPart("image") MultipartFile image) {
        try {
            String imagePath = imageService.saveImage("src/main/resources/images/promotions", image);
            Promotion createdPromotion = promotionService.createPromotion(request, imagePath);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Promotion created successfully.", createdPromotion));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to save image: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "An error occurred: " + e.getMessage(), null));
        }
    }

    // Get all promotions
    @GetMapping("/promotions")
    public ResponseEntity<ApiResponse<List<Promotion>>> getAllPromotions() {
        List<Promotion> promotions = promotionService.getAllPromotions();
        return ResponseEntity.ok(new ApiResponse<>(true, "Promotions retrieved successfully.", promotions));
    }

    // Get promotion by ID
    @GetMapping("/promotions/{id}")
    public ResponseEntity<ApiResponse<Promotion>> getPromotionById(@PathVariable UUID id) {
        return promotionService.getPromotionById(id)
                .map(promotion -> ResponseEntity.ok(new ApiResponse<>(true, "Promotion retrieved successfully.", promotion)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Promotion not found.", null)));
    }

    // Delete a promotion
    @DeleteMapping("/promotions")
    public ResponseEntity<ApiResponse<Void>> deletePromotion(@RequestBody UUID id) {
        try {
            promotionService.deletePromotion(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponse<>(true, "Promotion deleted successfully.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Promotion not found.", null));
        }
    }
}