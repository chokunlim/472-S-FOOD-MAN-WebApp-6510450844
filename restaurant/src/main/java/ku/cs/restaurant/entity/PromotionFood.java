package ku.cs.restaurant.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class PromotionFood {

    @EmbeddedId
    private PromotionFoodKey id;

    @ManyToOne
    @MapsId("promotionId")
    @JoinColumn(name = "p_id")
    private Promotion promotion;

    @ManyToOne
    @MapsId("foodId")
    @JoinColumn(name = "f_id")
    private Food food;
}