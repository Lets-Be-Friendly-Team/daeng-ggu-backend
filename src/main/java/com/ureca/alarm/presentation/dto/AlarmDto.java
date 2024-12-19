package com.ureca.alarm.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ureca.alarm.domain.Alarm;
import com.ureca.review.domain.Enum.AuthorType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

public class AlarmDto {
    @Builder(toBuilder = true)
    @Getter
    @Schema(name = "AlarmRequest")
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
    @Schema(name = "AlarmResponse")
    public static class Response {
        private Long alarmId; // 알림 ID
        private String alarmMessage; // 알림 메시지
        private String alarmType; // 알림 유형
        private Boolean alarmStatus; // 알림 상태
        private Long objectId;

        // Entity -> DTO 변환
        public static Response fromEntity(Alarm alarm) {
            return Response.builder()
                    .alarmId(alarm.getAlarmId())
                    .alarmMessage(alarm.getAlarmMessage())
                    .alarmType(alarm.getAlarmType())
                    .alarmStatus(alarm.getAlarmStatus())
                    .objectId(alarm.getObjectId())
                    .build();
        }
    }
    //    @JsonInclude(JsonInclude.Include.NON_NULL)
    //    @Builder(toBuilder = true)
    //    @Getter
    //    @AllArgsConstructor
    //    @NoArgsConstructor
    //    @Schema(name = "AlarmService")
    //    public static class Service {
    //        private Long objectId; //요청서,견적서,예약,리뷰 id
    //        private String alarmName;//견적요청,견적,예약,리뷰
    //        private String senderName;//개 이름, 미용실 이름 등
    //        private String senderUrl;//개 사진, 미용실 사진  등
    //        private String serviceName; // 미용/전체미용 등
    //
    //        public static Service fromEntity(Alarm alarm) {
    //            return Service.builder()
    //                    .objectId(alarm.getObjectId())
    //                    .alarmName(alarm.getAlarmMessage())
    //                    .senderName(alarm.getSenderName())
    //                    .senderUrl(alarm.getSenderUrl())
    //                    .serviceName(alarm.getServiceName())
    //                    .build();
    //        }
    //    }

}
