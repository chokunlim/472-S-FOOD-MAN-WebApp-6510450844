package ku.cs.restaurant.dto.promotion;

import ku.cs.restaurant.entity.PromotionType;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class PromotionResponseDTO {

    private UUID id;
    private String name;
    private String description;
    private PromotionType type;
    private double discountValue;
    private LocalDate startDate;
    private LocalDate endDate;
}
