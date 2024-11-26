package com.ureca.profile.domain;

import jakarta.persistence.*;
import lombok.*;

// 제공 서비스
@Entity
@Table(name = "services")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "designer")
public class Services {

    // 서비스 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;

    // 디자이너 아이디
    @ManyToOne
    @JoinColumn(name = "designerId", nullable = false)
    private Designer designer;

    // 제공 서비스 코드
    private String providedServicesCode;

    @Builder
    public Services(Long serviceId, Designer designer, String providedServicesCode) {
        this.serviceId = serviceId;
        this.designer = designer;
        this.providedServicesCode = providedServicesCode;
    }
}
