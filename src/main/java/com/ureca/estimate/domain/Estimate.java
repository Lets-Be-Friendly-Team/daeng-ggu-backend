package com.ureca.estimate.domain;

import com.ureca.common.entity.BaseEntity;
import com.ureca.profile.domain.Designer;
import com.ureca.request.domain.Request;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estimate")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Estimate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long estimate_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designer_id", nullable = false)
    private Designer designer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @Column(length = 200, nullable = false)
    private String estimate_detail;

    @Column private LocalDateTime desired_date;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal grooming_fee; // 미용비

    @Column(precision = 10, scale = 2)
    private BigDecimal delivery_fee; // 배송비 (NULL 가능)

    @Column(precision = 10, scale = 2)
    private BigDecimal monitoring_fee; // 모니터링비 (NULL 가능)

    @Column(precision = 10, scale = 2)
    private BigDecimal estimate_payment;

    @Column(length = 20)
    private String estimate_status;

    @OneToMany(mappedBy = "estimate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EstimateImage> estimateImages;
}
