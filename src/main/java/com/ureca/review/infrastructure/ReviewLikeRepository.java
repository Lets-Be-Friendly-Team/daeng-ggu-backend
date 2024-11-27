package com.ureca.review.infrastructure;

import com.ureca.review.domain.Enum.AuthorType;
import com.ureca.review.domain.Review;
import com.ureca.review.domain.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    ReviewLike findByReview(Review review);

    ReviewLike findByReviewIdAndUserIdAndUserType(Long reviewId, Long userId, AuthorType userType);
}
