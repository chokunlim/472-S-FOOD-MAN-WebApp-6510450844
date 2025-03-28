package ku.cs.restaurant.controller;

import ku.cs.restaurant.dto.ApiResponse;
import ku.cs.restaurant.dto.ReviewRequest;
import ku.cs.restaurant.entity.Order;
import ku.cs.restaurant.entity.OrderLine;
import ku.cs.restaurant.entity.Review;
import ku.cs.restaurant.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/review/submit")
    public ResponseEntity<Review> submitReview(@RequestBody ReviewRequest reviewRequest) {
        Review review = reviewService.submitReview(
                reviewRequest.getOrderId(),
                reviewRequest.getRating(),
                reviewRequest.getComment()
        );
        return ResponseEntity.ok(review);
    }

    @GetMapping("/review")
    public ResponseEntity<ApiResponse<List<Review>>> getReviews() {
        try {
            List<Review> reviewList = reviewService.getReviews();
            return ResponseEntity.ok(new ApiResponse<>(true, "Review List fetched successfully.", reviewList));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "An error occurred: " + e.getMessage(), null));
        }
    }
}