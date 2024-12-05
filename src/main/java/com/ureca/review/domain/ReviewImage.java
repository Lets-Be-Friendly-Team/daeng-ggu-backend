package com.ureca.review.domain;

import com.ureca.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review_image")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ReviewImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewImageId;

    @Column(length = 300, nullable = false)
    private String reviewImageUrl; // 이미지 URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewId")
    private Review review; // Review와의 연관 관계
}
