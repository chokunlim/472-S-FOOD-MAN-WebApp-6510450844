package ku.cs.restaurant.dto.promotion;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class PromotionDTO {

    private String name;
    private String description;
    private double price;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<UUID> foodIds;  // ใช้สำหรับระบุอาหารที่เกี่ยวข้องในโปรโมชั่น
}
