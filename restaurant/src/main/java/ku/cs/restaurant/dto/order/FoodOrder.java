package ku.cs.restaurant.dto.order;

import lombok.Data;
import java.util.UUID;

@Data
public class FoodOrder {
    private UUID foodId;
    private int quantity;
}