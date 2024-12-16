package com.ureca.monitoring.domain;

import com.ureca.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "process")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Process extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long processId; // 프로세스 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guardian_id")
    private Guardian guardian; // 가디언 ID (연관관계)

    @Column(nullable = false)
    private Long customerId; // 보호자 아이디

    @Column(nullable = false)
    @Builder.Default
    private Integer processNum = 1; // 진행 단계, 기본값 1

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProcessStatus processStatus; // 진행 상태

    @Column(length = 255)
    private String processMessage; // 진행 상태 메시지

    @Column(length = 255)
    private String playbackUrl; // 플레이백 URL

    @Column(length = 255)
    private String streamKey; // 스트림 Key
}
