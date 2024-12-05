package com.ureca.review.domain;

import com.ureca.common.entity.BaseEntity;
import com.ureca.review.domain.Enum.AuthorType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "review_like")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ReviewLike extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewLikeId;

    @Column(nullable = false)
    private Long userId; // 좋아요한사람 Id

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private AuthorType userType; // 좋아요한사람 Type

    @Column(nullable = false)
    private Boolean isReviewLike;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewId")
    private Review review; // Review와의 연관 관계
}
