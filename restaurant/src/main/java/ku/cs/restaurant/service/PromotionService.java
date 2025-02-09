package ku.cs.restaurant.service;

import ku.cs.restaurant.dto.promotion.PromotionCreateRequest;
import ku.cs.restaurant.entity.Promotion;
import ku.cs.restaurant.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PromotionService {
    private final PromotionFoodService promotionFoodService;
    private final PromotionRepository promotionRepository;

    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    public Optional<Promotion> getPromotionById(UUID id) {
        return promotionRepository.findById(id);
    }

    public Promotion createPromotion(PromotionCreateRequest promotionCreateRequest) {
        Promotion promotion = new Promotion();
        promotion.setName(promotionCreateRequest.getName());
        promotion.setDescription(promotionCreateRequest.getDescription());
        promotion.setType(promotionCreateRequest.getType());
        promotion.setDiscountValue(promotionCreateRequest.getDiscountValue());
        promotion.setEndDate(promotionCreateRequest.getEndDate());
        promotion.setStartDate(promotionCreateRequest.getStartDate());


        // บันทึกโปรโมชั่นใหม่
        Promotion createdPromotion = promotionRepository.save(promotion);

        // เชื่อมโยงอาหารที่ถูกเลือกให้กับโปรโมชั่น
        for (UUID foodId : promotionCreateRequest.getFoodIds()) {
            promotionFoodService.addFoodToPromotion(createdPromotion.getId(), foodId);
        }

        return createdPromotion;
    }

    public void deletePromotion(UUID id) {
        promotionRepository.deleteById(id);
    }
}
