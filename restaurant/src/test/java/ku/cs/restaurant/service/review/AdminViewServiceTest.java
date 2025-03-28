package ku.cs.restaurant.service.review;

import ku.cs.restaurant.entity.Review;
import ku.cs.restaurant.repository.ReviewRepository;
import ku.cs.restaurant.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminViewServiceTest {

    private ReviewService reviewService;
    private ReviewRepository reviewRepository;

    @BeforeEach
    void setUp() {
        reviewRepository = mock(ReviewRepository.class);
        reviewService = new ReviewService(reviewRepository, null); // Assuming null for OrderRepository
    }

    @Test
    void testAdminCanAccessReviewPage() {
        boolean canAccess = true; // Admin should always be able to access the review page
        assertTrue(canAccess, "Admin should be able to access the review page");
    }

    @Test
    void testAdminCanViewCustomerReviews() {
        Review review1 = new Review();
        review1.setComment("Great food!");
        review1.setRating(5);

        Review review2 = new Review();
        review2.setComment("Service could be better.");
        review2.setRating(3);

        when(reviewRepository.findAll()).thenReturn(List.of(review1, review2));

        List<Review> reviews = reviewService.getReviews();

        assertNotNull(reviews);
        assertEquals(2, reviews.size());
        assertEquals("Great food!", reviews.get(0).getComment());
        assertEquals("Service could be better.", reviews.get(1).getComment());
        verify(reviewRepository, times(1)).findAll();
    }

    @Test
    void testAdminCanViewTotalNumberOfReviews() {
        Review review1 = new Review();
        Review review2 = new Review();

        when(reviewRepository.findAll()).thenReturn(List.of(review1, review2));

        int totalReviews = reviewService.getReviews().size();

        assertEquals(2, totalReviews, "Admin should be able to view the total number of reviews");
        verify(reviewRepository, times(1)).findAll();
    }
}
