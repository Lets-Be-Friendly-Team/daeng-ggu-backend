package com.ureca.review.infrastructure;

import com.ureca.review.domain.Enum.AuthorType;
import com.ureca.review.domain.Review;
import com.ureca.review.domain.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    ReviewLike findByReviewAndUserIdAndUserType(Review review, Long userId, AuthorType userType);
}
