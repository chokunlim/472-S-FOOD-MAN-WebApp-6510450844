package ku.cs.restaurant.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class Promotion {
    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "p_id")
    private UUID id;

    @Column(name = "p_name")
    private String name;

    @Column(name = "p_description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "p_type")
    private PromotionType type;

    @Column(name = "p_discount_value")
    private double discountValue;

    @Column(name = "p_start_date")
    private LocalDate startDate;

    @Column(name = "p_end_date")
    private LocalDate endDate;
    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PromotionFood> promotionFoods;

}
