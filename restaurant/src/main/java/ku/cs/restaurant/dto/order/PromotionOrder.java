package ku.cs.restaurant.dto.order;

import lombok.Data;

import java.util.UUID;

@Data
public class PromotionOrder {
    private UUID promotionId;
    private int quantity;
}