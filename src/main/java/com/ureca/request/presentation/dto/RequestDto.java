package com.ureca.request.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RequestDto {
    @Builder(toBuilder = true)
    @Getter
    @Schema(name = "RequestRequest")
    public static class Request {
        private Long petId;
        private String desiredServiceCode;
        private String lastGroomingDate;
        private LocalDateTime desiredDate1;
        private LocalDateTime desiredDate2;
        private LocalDateTime desiredDate3;
        private String desiredRegion;
        private Boolean isVisitRequired;
        private Boolean isMonitoringIncluded;
        private String additionalRequest;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder(toBuilder = true)
    @Getter
    @Schema(name = "RequestResponse")
    public static class Response {
        private Long requestId;
        private Long petId;
        private String petName;
        private String petImageUrl;
        private Boolean isPetRequested;
        private String petImageName;
        private String birthDate; // 생년월일 (YYYYMMDD)
        private String gender; // 성별 (M/W)
        private String isNeutered; // 중성화 여부 (Y/N)
        private Double weight; // 몸무게
        private String majorBreedCode; // 견종 대분류 코드
        private String majorBreed; // 견종 대분류명
        private String subBreedCode; // 견종 소분류 코드
        private String subBreed; // 견종 소분류명
        private String specialNotes; // 특이사항
        private Boolean isRequested; // 견적요청여부
        private Long customerId; // 보호자 아이디
        private String customerName; // 보호자명
        private String phone; // 전화번호
        private String address; // 주소
        private String desiredServiceCode;
        private String lastGroomingDate;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime desiredDate1;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime desiredDate2;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime desiredDate3;

        private String desiredRegion;
        private Boolean isVisitRequired;
        private Boolean isMonitoringIncluded;
        private String additionalRequest;
        private BigDecimal deliveryFee;
        private BigDecimal monitoringFee;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime createdAt;

        private String codeName;
    }

    @Builder(toBuilder = true)
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "RequestID")
    public static class ID {
        private Long requestId;
    }
}
