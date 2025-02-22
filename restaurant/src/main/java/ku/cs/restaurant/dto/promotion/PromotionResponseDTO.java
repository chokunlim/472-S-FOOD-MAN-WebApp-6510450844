package ku.cs.restaurant.dto.promotion;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class PromotionResponseDTO {

    private UUID id;
    private String name;
    private String description;
    private double price;
    private LocalDate startDate;
    private LocalDate endDate;
}
