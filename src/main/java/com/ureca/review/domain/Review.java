package com.ureca.review.domain;

import com.ureca.profile.domain.Customer;
import com.ureca.profile.domain.Designer;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column private String reviewContents;

    @Column private Integer reviewStar;

    @Column private Boolean isFeedAdd; // 피드 참여 여부

    @Column(nullable = true)
    private String feedUrl; // 피드 프사?

    @Column @Builder.Default private Integer reviewLikeCnt = 0;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdat;

    @LastModifiedDate private LocalDateTime updatedat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designer_id")
    private Designer designer;

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
}
