package ku.cs.restaurant.dto.order;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private List<FoodOrder> foods;
    private List<PromotionOrder> promotions;
}