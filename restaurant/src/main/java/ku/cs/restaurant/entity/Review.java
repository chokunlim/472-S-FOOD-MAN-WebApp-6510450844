package ku.cs.restaurant.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Review {
    @Id
    @GeneratedValue
    @Column(unique = true, nullable = false, name = "review_id")
    private UUID id;

    private int rating;

    private String comment;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
