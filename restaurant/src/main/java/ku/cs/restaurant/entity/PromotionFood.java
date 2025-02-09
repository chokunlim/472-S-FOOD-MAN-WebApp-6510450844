package ku.cs.restaurant.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class PromotionFood {

    @EmbeddedId
    private PromotionFoodKey id;  // Composite Key (promotionId + foodId)

    // Many-to-One relation with Promotion
    @ManyToOne
    @MapsId("promotionId")  // Maps the promotionId from the composite key
    @JoinColumn(name = "p_id")
    private Promotion promotion;

    // Many-to-One relation with Food
    @ManyToOne
    @MapsId("foodId")  // Maps the foodId from the composite key
    @JoinColumn(name = "f_id")
    private Food food;
}
