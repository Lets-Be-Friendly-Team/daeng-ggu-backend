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
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 고객 아이디로 해당 고객의 모든 리뷰를 찾기
    List<Review> findByCustomerCustomerId(Long customerId);

    // 디자이너 아이디로 해당 디자이너의 모든 리뷰를 찾기
    List<Review> findByDesignerDesignerId(Long designerId);

    // 특정 고객과 디자이너에 대한 리뷰를 찾기
    List<Review> findByCustomerCustomerIdAndDesignerDesignerId(Long customerId, Long designerId);

    // 리뷰 별점으로 정렬해서 리뷰 목록을 가져오기 (예시)
    List<Review> findByReviewStarOrderByCreatedAtDesc(Integer reviewStar);

    List<Review> findByDesignerOrderByCreatedAtDesc(Designer designer);

    List<Review> findByCreatedAtBefore(LocalDateTime createdAt, Pageable pageable);

    @Query("SELECT r FROM Review r ORDER BY r.createdAt DESC")
    List<Review> findTopN(@Param("size") int size);

    List<Review> findByCustomerOrderByCreatedAtDesc(Customer customer);


}
