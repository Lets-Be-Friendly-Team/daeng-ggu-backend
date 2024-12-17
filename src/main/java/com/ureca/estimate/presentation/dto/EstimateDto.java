package com.ureca.estimate.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class EstimateDto {

    @Builder(toBuilder = true)
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "EstimateCreate")
    public static class Create {
        private Request estimateRequest;
        private List<Img> estimateImgList;
        private List<TagId> estimateImgIdList;
    }

    @Builder(toBuilder = true)
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "EstimateRequest")
    public static class Request {
        private Long requestId;
        private String requestDetail;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime requestDate;

        private BigDecimal requestPrice;
    }

    @Builder(toBuilder = true)
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "EstimateImg")
    public static class Img {
        private String estimateImgUrl;
    }

    @Builder(toBuilder = true)
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "EstimateTagId")
    public static class TagId {
        private String estimateTagId;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder(toBuilder = true)
    @Getter
    @Schema(name = "EstimateResponse")
    public static class Response {
        private Long estimateId;
        private Long petId;
        private String petName;
        private String petImageUrl;
        private String majorBreedCode;
        private String desiredServiceCode;
        private String lastGrommingDate;
        private LocalDateTime desiredDate1;
        private LocalDateTime desiredDate2;
        private LocalDateTime desiredDate3;
        private String desiredRegion;
        private Boolean isVisitRequired;
        private Boolean isMonitoringIncluded;
        private String additionalRequest;
        private LocalDateTime createdAt;
        private List<EstimateDtoDetail> estimateList;
    }

    @Builder(toBuilder = true)
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "EstimateID")
    public static class ID {
        private Long estimateId;
    }
}
