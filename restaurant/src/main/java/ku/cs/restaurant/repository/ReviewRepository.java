package ku.cs.restaurant.repository;

import ku.cs.restaurant.entity.Order;
import ku.cs.restaurant.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByOrder(Order order);
}
