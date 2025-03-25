package ku.cs.restaurant.controller;

import ku.cs.restaurant.dto.ApiResponse;
import ku.cs.restaurant.dto.promotion.PromotionCreateRequest;
import ku.cs.restaurant.entity.Food;
import ku.cs.restaurant.entity.Promotion;
import ku.cs.restaurant.service.ImageService;
import ku.cs.restaurant.service.PromotionFoodService;
import ku.cs.restaurant.service.PromotionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/promotions")
public class PromotionController {
    private final PromotionService promotionService;
    private final ImageService imageService;
    private final PromotionFoodService promotionFoodService;
    private static final String BASE_IMAGE_URL = "http://localhost:8088/images/promotions/";

    public PromotionController(PromotionService promotionService, ImageService imageService, PromotionFoodService promotionFoodService) {
        this.promotionService = promotionService;
        this.imageService = imageService;
        this.promotionFoodService = promotionFoodService;
    }

    // ✅ สร้างโปรโมชั่น (พร้อมอาหาร)
    @PostMapping
    @Transactional
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

    @GetMapping
    public ResponseEntity<ApiResponse<List<Promotion>>> getAllPromotions() {
        List<Promotion> promotions = promotionService.getAllPromotions();

        // ✅ อัปเดตพาธรูปภาพให้เป็น URL ที่เข้าถึงได้
        promotions.forEach(promo -> {
            if (promo.getImagePath() != null) {
                String imagePath = promo.getImagePath().replace("\\", "/");
                String fileName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
                promo.setImagePath(BASE_IMAGE_URL + fileName);
            }
        });

        return ResponseEntity.ok(new ApiResponse<>(true, "Fetched all promotions.", promotions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPromotionById(@PathVariable UUID id) {
        Optional<Promotion> promotionOpt = promotionService.getPromotionById(id);

        if (promotionOpt.isPresent()) {
            Promotion promotion = promotionOpt.get();

            if (promotion.getImagePath() != null) {
                String imagePath = promotion.getImagePath().replace("\\", "/");
                String fileName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
                promotion.setImagePath(BASE_IMAGE_URL + fileName);
            }

            List<Food> foods = promotionFoodService.getFoodsByPromotion(id);

            Map<String, Object> responseData = Map.of(
                    "promotion", promotion,
                    "foods", foods
            );

            return ResponseEntity.ok(new ApiResponse<>(true, "Promotion found.", responseData));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Promotion not found.", null));
        }
    }


    // ✅ ลบโปรโมชั่น (พร้อมลบรูปภาพ)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePromotion(@PathVariable UUID id) {
        boolean deleted = promotionService.deletePromotion(id);
        if (deleted) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Promotion deleted successfully.", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Promotion not found.", null));
        }
    }
    // ✅ แก้ไขโปรโมชั่น
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Promotion>> updatePromotion(@PathVariable UUID id,
                                                                  @RequestPart("promotion") PromotionCreateRequest request,
                                                                  @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            String imagePath = null;
            if (image != null && !image.isEmpty()) {
                imagePath = imageService.saveImage("src/main/resources/images/promotions", image);
            }

            // เรียกใช้ฟังก์ชัน updatePromotion ใน PromotionService
            Promotion updatedPromotion = promotionService.updatePromotion(id, request, imagePath);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse<>(true, "Promotion updated successfully.", updatedPromotion));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to save image: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "An error occurred: " + e.getMessage(), null));
        }
    }

}
