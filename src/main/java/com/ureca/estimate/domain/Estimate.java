package com.ureca.estimate.domain;

import com.ureca.profile.domain.Designer;
import com.ureca.request.domain.Request;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estimate")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Estimate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long estimateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designerId", nullable = false)
    private Designer designer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestId", nullable = false)
    private Request request;

    @Column(length = 200, nullable = false)
    private String estimateDetail;

    @Column private LocalDateTime desiredDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal estimatePayment;

    @Column(length = 20)
    private String estimateStatus;
}
