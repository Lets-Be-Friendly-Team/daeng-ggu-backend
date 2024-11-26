package com.example.daengguubackend.review.domain;



import com.example.daengguubackend.designer.domain.Designer;
import com.example.daengguubackend.customer.domain.Customer;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column
    private String reviewContents;

    @Column
    private Integer reviewStar;

    @Column
    private Boolean is_feedAdd; // 피드 참여 여부

    @Column(nullable = true)
    private String feedUrl; // 피드 프사?

    @Column
    @Builder.Default
    private Integer reviewLikeCnt = 0;


    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdat;
    @LastModifiedDate
    private LocalDateTime updatedat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designer_id")
    private Designer designer;
}

