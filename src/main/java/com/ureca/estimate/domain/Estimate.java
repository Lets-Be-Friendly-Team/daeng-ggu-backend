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
    private Long estimateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designer_id", nullable = false)
    private Designer designer;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @Column(length = 500, nullable = false)
    private String estimateDetail;

    @Column private LocalDateTime desiredDate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal groomingFee; // 미용비

    @Column(precision = 10, scale = 2)
    private BigDecimal estimatePayment;

    @Column(length = 20)
    private String estimateStatus;

    @OneToMany(mappedBy = "estimate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EstimateImage> estimateImages;
}
