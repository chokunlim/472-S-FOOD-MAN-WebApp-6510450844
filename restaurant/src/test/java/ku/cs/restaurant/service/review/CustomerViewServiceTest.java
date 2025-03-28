package ku.cs.restaurant.service.review;

import ku.cs.restaurant.entity.Order;
import ku.cs.restaurant.entity.Review;
import ku.cs.restaurant.repository.OrderRepository;
import ku.cs.restaurant.repository.ReviewRepository;
import ku.cs.restaurant.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


class CustomerViewServiceTest {

    private ReviewService reviewService;
    private ReviewRepository reviewRepository;
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        reviewRepository = mock(ReviewRepository.class);
        orderRepository = mock(OrderRepository.class);
        reviewService = new ReviewService(reviewRepository, orderRepository);
    }

    @Test
    void testCustomerCanGiveRating() {
        UUID orderId = UUID.randomUUID();
        int rating = 5;
        String comment = "Great service!";
        Order mockOrder = mock(Order.class);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Review review = reviewService.submitReview(orderId, rating, comment);

        assertNotNull(review);
        assertEquals(rating, review.getRating());
        assertEquals(comment, review.getComment());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testCustomerCanWriteReview() {
        UUID orderId = UUID.randomUUID();
        int rating = 4;
        String comment = "Delicious food!";
        Order mockOrder = mock(Order.class);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Review review = reviewService.submitReview(orderId, rating, comment);

        assertNotNull(review);
        assertEquals(comment, review.getComment());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testCustomerCanViewReviews() {
        Review review1 = new Review();
        review1.setComment("Amazing experience!");
        review1.setRating(5);

        Review review2 = new Review();
        review2.setComment("Good food but slow service.");
        review2.setRating(3);

        when(reviewRepository.findAll()).thenReturn(List.of(review1, review2));

        List<Review> reviews = reviewService.getReviews();

        assertNotNull(reviews);
        assertEquals(2, reviews.size());
        assertEquals("Amazing experience!", reviews.get(0).getComment());
        assertEquals("Good food but slow service.", reviews.get(1).getComment());
        verify(reviewRepository, times(1)).findAll();
    }
}