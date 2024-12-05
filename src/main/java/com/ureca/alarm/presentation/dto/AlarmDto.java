package com.ureca.alarm.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ureca.review.domain.Enum.AuthorType;
import lombok.Builder;
import lombok.Getter;

public class AlarmDto {
    @Builder(toBuilder = true)
    @Getter
    public static class Request{
        private Long senderId;
        private AuthorType senderType;
        private Long receiverId;
        private AuthorType receiverType;
        private Long objectId;
        private String alarm_message;
        private String alarm_type;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder(toBuilder = true)
    @Getter
    public static class Response {

    }
}
