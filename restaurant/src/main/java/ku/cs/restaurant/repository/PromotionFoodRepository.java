package ku.cs.restaurant.repository;

import ku.cs.restaurant.entity.PromotionFood;
import ku.cs.restaurant.entity.PromotionFoodKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PromotionFoodRepository extends JpaRepository<PromotionFood, PromotionFoodKey> {
    List<PromotionFood> findByPromotionId(UUID promotionId);
    // เพิ่มฟังก์ชันที่จำเป็นหากต้องการค้นหาหรือจัดการตารางกลางโดยตรง
}