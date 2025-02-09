package ku.cs.restaurant.service;

import ku.cs.restaurant.entity.Promotion;
import ku.cs.restaurant.entity.Food;
import ku.cs.restaurant.entity.PromotionFood;
import ku.cs.restaurant.entity.PromotionFoodKey;
import ku.cs.restaurant.repository.PromotionRepository;
import ku.cs.restaurant.repository.FoodRepository;
import ku.cs.restaurant.repository.PromotionFoodRepository; // สำหรับการจัดการตารางกลาง (หากจำเป็น)
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PromotionFoodService {

    private final PromotionFoodRepository promotionFoodRepository;
    private final FoodRepository foodRepository;
    private final PromotionRepository promotionRepository;

    // ฟังก์ชันในการเพิ่มการเชื่อมโยงระหว่าง Promotion และ Food
    public void addFoodToPromotion(UUID promotionId, UUID foodId) {
        Promotion promotion = promotionRepository.findById(promotionId).orElseThrow();
        Food food = foodRepository.findById(foodId).orElseThrow();

        PromotionFoodKey key = new PromotionFoodKey(promotionId, foodId);
        PromotionFood promotionFood = new PromotionFood();
        promotionFood.setId(key);
        promotionFood.setPromotion(promotion);
        promotionFood.setFood(food);

        promotionFoodRepository.save(promotionFood);
    }
}
