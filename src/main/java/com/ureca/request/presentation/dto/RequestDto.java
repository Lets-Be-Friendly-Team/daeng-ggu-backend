package com.ureca.request.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class RequestDto {
    @Builder(toBuilder = true)
    @Getter
    public static class Request{
        private Long designer_id;
        private Long pet_id;
        private Long request_id;
        private String desired_service_code;
        private String last_grooming_date;
        private LocalDateTime desired_date1;
        private LocalDateTime desired_date2;
        private LocalDateTime desired_date3;
        private String desired_region;
        private Boolean is_delivery;
        private Boolean is_monitoringIncluded;
        private String additional_request;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder(toBuilder = true)
    @Getter
    public static class Response{
        private Long pet_id;
        private String desired_service_code;
        private String last_grooming_date;
        private LocalDateTime desired_date1;
        private LocalDateTime desired_date2;
        private LocalDateTime desired_date3;
        private String desired_region;
        private Boolean is_delivery;
        private Boolean is_monitoringIncluded;
        private String additional_request;
    }

}
