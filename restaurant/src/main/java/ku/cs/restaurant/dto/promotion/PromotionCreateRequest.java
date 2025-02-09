package ku.cs.restaurant.dto.promotion;

import ku.cs.restaurant.entity.PromotionType;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class PromotionCreateRequest {
    private String name;
    private String description;
    private PromotionType type;  // PERCENTAGE หรือ FIXED_AMOUNT
    private double discountValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<UUID> foodIds; // รายการอาหารที่อยู่ในโปรโมชั่น
}
