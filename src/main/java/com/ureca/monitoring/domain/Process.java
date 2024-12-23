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

    @Column(nullable = true)
    private String channelARN; // 스트림 Key-> channelARN

    public void updateStatus(
            Integer processNum, ProcessStatus processStatus, String processMessage) {
        this.processNum = processNum;
        this.processStatus = processStatus;
        this.processMessage = processMessage;
    }

    public void updateStreamValue(String playbackUrl, String channelARN) {
        this.playbackUrl = playbackUrl;
        this.channelARN = channelARN;
    }
}
