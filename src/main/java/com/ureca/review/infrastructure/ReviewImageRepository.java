package com.ureca.review.infrastructure;

import com.ureca.review.domain.Review;
import com.ureca.review.domain.ReviewImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    List<ReviewImage> findByReview(Review review);

    ReviewImage findByReviewImageUrl(String deleteImg);
}
