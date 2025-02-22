package ku.cs.restaurant.service;

import ku.cs.restaurant.entity.Food;
import ku.cs.restaurant.entity.Promotion;
import ku.cs.restaurant.entity.PromotionFood;
import ku.cs.restaurant.entity.PromotionFoodKey;
import ku.cs.restaurant.repository.PromotionRepository;
import ku.cs.restaurant.repository.FoodRepository;
import ku.cs.restaurant.repository.PromotionFoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionFoodService {
    private final PromotionFoodRepository promotionFoodRepository;
    private final FoodRepository foodRepository;
    private final PromotionRepository promotionRepository;

    // เพิ่มอาหารเข้าโปรโมชั่น
    public void addFoodToPromotion(UUID promotionId, UUID foodId) {
        Promotion promotion = promotionRepository.findById(promotionId).orElseThrow(() -> new RuntimeException("Promotion not found"));
        Food food = foodRepository.findById(foodId).orElseThrow(() -> new RuntimeException("Food not found"));

        PromotionFoodKey key = new PromotionFoodKey(promotionId, foodId);
        PromotionFood promotionFood = new PromotionFood();
        promotionFood.setId(key);
        promotionFood.setPromotion(promotion);
        promotionFood.setFood(food);

        promotionFoodRepository.save(promotionFood);
    }

    // ดึงรายการอาหารในโปรโมชั่น
    public List<Food> getFoodsByPromotion(UUID promotionId) {
        List<PromotionFood> promotionFoods = promotionFoodRepository.findByPromotionId(promotionId);
        return promotionFoods.stream()
                .map(PromotionFood::getFood)
                .collect(Collectors.toList());
    }
}
