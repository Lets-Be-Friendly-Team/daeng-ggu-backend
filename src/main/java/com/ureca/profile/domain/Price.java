package com.ureca.profile.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

// 서비스별 견종 미용 금액
@Entity
@Table(name = "price")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(exclude = {"breed", "service"})
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long priceId; // 자동 증가되는 price_id

    @ManyToOne(fetch = FetchType.LAZY) // Breed 엔티티와의 다대일 관계
    @JoinColumn(name = "possible_id", nullable = false)
    private Breeds breed; // 견종 ID (Breeds 엔티티와 연관)

    @ManyToOne(fetch = FetchType.LAZY) // Service 엔티티와의 다대일 관계
    @JoinColumn(name = "service_id", nullable = false)
    private Services service; // 서비스 ID (Services 엔티티와 연관)

    private BigDecimal price; // 가격
    private Integer time; // 시간
}
