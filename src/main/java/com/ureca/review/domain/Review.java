package com.ureca.review.domain;

import com.ureca.common.entity.BaseEntity;
import com.ureca.profile.domain.Customer;
import com.ureca.profile.domain.Designer;
import jakarta.persistence.*;
import java.util.List;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "review")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(length = 200)
    private String reviewContents;

    @Column(nullable = false)
    private Double reviewStar;

    @Column(nullable = false)
    private Boolean isFeedAdd; // 피드 참여 여부

    @Column(length = 300)
    private String feedUrl; // 피드 썸네일 URL

    @Column @Builder.Default private Integer reviewLikeCnt = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designerId")
    private Designer designer;

    // 리뷰사진 연관 관계 (1:N)
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImage> reviewImages;

    public Review increaseReviewLikeCnt() {
        reviewLikeCnt += 1;
        return this;
    }

    public Review decreaseReviewLikeCnt() {
        reviewLikeCnt -= 1;
        return this;
    }

    public Review updateReviewContents(String newContents) {
        return this.toBuilder()
                .reviewContents(newContents) // 내용만 변경
                .build();
    }

    public Review updateReviewStar(Double newStar) {
        return this.toBuilder()
                .reviewStar(newStar) // 별점만 변경
                .build();
    }
}
