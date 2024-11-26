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
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column
    private String reviewContents;

    @Column(nullable = false)
    private Integer reviewStar;

    @Column(nullable = false)
    private Boolean is_feedAdd; // 피드 참여 여부

    @Column(nullable = false)
    private String feedUrl; // 피드 썸네일

    @Column @Builder.Default private Integer reviewLikeCnt = 0;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdat;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designerId")
    private Designer designer;

}
