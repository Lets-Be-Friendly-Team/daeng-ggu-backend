package com.ureca.profile.domain;

import jakarta.persistence.*;
import lombok.*;

// 제공 서비스
@Entity
@Table(name = "services")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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
}
