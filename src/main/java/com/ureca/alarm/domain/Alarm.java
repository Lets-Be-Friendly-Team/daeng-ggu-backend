package com.ureca.alarm.domain;

import com.ureca.common.entity.BaseEntity;
import com.ureca.review.domain.Enum.AuthorType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "alarm")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Alarm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Long alarmId;

    @Column(name = "sender_id")
    private Long senderId; // 보낸 사람 ID (NULL 가능)

    @Enumerated(EnumType.STRING)
    @Column(name = "sender_type")
    private AuthorType senderType; // 보낸 사람 유형 (CUSTOMER, DESIGNER)

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId; // 받는 사람 ID

    @Enumerated(EnumType.STRING)
    @Column(name = "receiver_type", nullable = false)
    private AuthorType receiverType; // 받는 사람 유형 (CUSTOMER, DESIGNER)

    @Column(name = "object_id")
    private Long objectId; // 알람 대상 ID(request_id,review_id, reservation_id)

    @Column(length = 100, nullable = true)
    private String alarmMessage; // 알림 내용

    @Column(length = 20, nullable = false)
    private String alarmType; // 알림 유형 (예: "REQUEST", "ESTIMATE" , "RESERVATION" , "REVIEW")

    @Builder.Default
    @Column(nullable = false)
    private Boolean alarmStatus = false;
}
