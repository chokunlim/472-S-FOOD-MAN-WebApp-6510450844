package ku.cs.restaurant.controller;

import ku.cs.restaurant.dto.ReviewRequest;
import ku.cs.restaurant.entity.Review;
import ku.cs.restaurant.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/submit")
    public ResponseEntity<Review> submitReview(@RequestBody ReviewRequest reviewRequest) {
        Review review = reviewService.submitReview(
                reviewRequest.getOrderId(),
                reviewRequest.getRating(),
                reviewRequest.getComment()
        );
        return ResponseEntity.ok(review);
    }
}