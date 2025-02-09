package ku.cs.restaurant.controller;
import ku.cs.restaurant.dto.promotion.PromotionCreateRequest;
import ku.cs.restaurant.dto.promotion.PromotionResponseDTO;
import ku.cs.restaurant.entity.Promotion;
import ku.cs.restaurant.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    // Method to convert Entity to DTO
    private PromotionResponseDTO mapToDTO(Promotion promotion) {
        PromotionResponseDTO dto = new PromotionResponseDTO();
        dto.setId(promotion.getId());
        dto.setName(promotion.getName());
        dto.setDescription(promotion.getDescription());
        dto.setType(promotion.getType());
        dto.setDiscountValue(promotion.getDiscountValue());
        dto.setStartDate(promotion.getStartDate());
        dto.setEndDate(promotion.getEndDate());
        return dto;
    }

    // Get all promotions
    @GetMapping
    public List<PromotionResponseDTO> getAllPromotions() {
        List<Promotion> promotions = promotionService.getAllPromotions();
        return promotions.stream()
                .map(this::mapToDTO) // แปลงจาก Entity เป็น DTO
                .toList();
    }

    // Get promotion by ID
    @GetMapping("/{id}")
    public ResponseEntity<PromotionResponseDTO> getPromotionById(@PathVariable UUID id) {
        Optional<Promotion> promotion = promotionService.getPromotionById(id);
        return promotion.map(p -> ResponseEntity.ok(mapToDTO(p))) // แปลง Entity เป็น DTO
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Create new promotion
    @PostMapping
    public ResponseEntity<PromotionResponseDTO> createPromotion(@RequestBody PromotionCreateRequest promotionCreateRequest) {
        // แปลงจาก DTO เป็น Entity
        // จัดการอาหารที่เกี่ยวข้อง
        // promotion.setFoods( ... ); // หากต้องการเชื่อมโยงอาหารกับโปรโมชั่น

        // สร้างโปรโมชั่นใหม่
        Promotion createdPromotion = promotionService.createPromotion(promotionCreateRequest);

        // แปลงจาก Entity เป็น DTO และส่งกลับ
        PromotionResponseDTO responseDTO = mapToDTO(createdPromotion);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    // Delete promotion by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable UUID id) {
        Optional<Promotion> promotion = promotionService.getPromotionById(id);
        if (promotion.isPresent()) {
            promotionService.deletePromotion(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
