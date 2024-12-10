package com.ureca.alarm.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ureca.alarm.domain.Alarm;
import com.ureca.review.domain.Enum.AuthorType;
import lombok.Builder;
import lombok.Getter;

public class AlarmDto {
    @Builder(toBuilder = true)
    @Getter
    public static class Request {
        private Long alarmId;
        private Long senderId;
        private AuthorType senderType;
        private Long receiverId;
        private AuthorType receiverType;
        private Long objectId;
        private String alarmMessage;
        private String alarmType;

        @Builder.Default private Integer page = 0;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder(toBuilder = true)
    @Getter
    public static class Response {
        private Long alarmId; // 알림 ID
        private String alarmMessage; // 알림 메시지
        private String alarmType; // 알림 유형
        private Boolean alarmStatus; // 알림 상태
        private String senderUrl;

        // Entity -> DTO 변환
        public static Response fromEntity(Alarm alarm) {
            return Response.builder()
                    .alarmId(alarm.getAlarmId())
                    .alarmMessage(alarm.getAlarmMessage())
                    .alarmType(alarm.getAlarmType())
                    .alarmStatus(alarm.getAlarmStatus())
                    .build();
        }
    }
}
