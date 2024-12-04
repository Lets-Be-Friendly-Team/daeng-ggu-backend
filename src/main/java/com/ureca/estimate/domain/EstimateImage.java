package com.ureca.estimate.domain;

import com.ureca.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estimate_image")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class EstimateImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long estimate_img_id;

    @Column(length = 300, nullable = false)
    private String estimate_img_url; // 이미지 URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estimate_id")
    private Estimate estimate; // Review와의 연관 관계
}
