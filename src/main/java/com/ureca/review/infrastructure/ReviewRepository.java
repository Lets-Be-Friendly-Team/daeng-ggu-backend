package com.ureca.review.infrastructure;

import com.ureca.profile.domain.Customer;
import com.ureca.profile.domain.Designer;
import com.ureca.review.domain.Review;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByDesignerOrderByCreatedAtDesc(Designer designer);

    List<Review> findByCreatedAtBefore(LocalDateTime createdAt, Pageable pageable);

    @Query("SELECT r FROM Review r ORDER BY r.createdAt DESC")
    List<Review> findTopN(@Param("size") int size);

    List<Review> findByCustomerOrderByCreatedAtDesc(Customer customer);
}
