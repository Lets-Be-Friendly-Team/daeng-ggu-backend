package com.example.daengguubackend.review.domain;

import com.example.daengguubackend.review.domain.Enum.AuthorType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ReviewLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewLikeId;

    @Column(nullable = false)
    private String userId; // 좋아요한사람 Id

    @Column(nullable = false)
    private AuthorType userType; // 좋아요한사람 Type

    @Setter
    @Column
    private Boolean is_reviewLike;

    @ManyToOne(fetch = FetchType.LAZY)
    private Review review; // Review와의 연관 관계

}
