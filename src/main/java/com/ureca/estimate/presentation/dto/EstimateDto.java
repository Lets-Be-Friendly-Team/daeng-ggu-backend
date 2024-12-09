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
import org.springframework.web.multipart.MultipartFile;

public class EstimateDto {

    @Builder(toBuilder = true)
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "EstimateRequest")
    public static class Request {
        private Long estimateId;
        private Long requestId;
        private Long customerId;
        private Long designerId;
        private Long petId;
        private String requestDetail;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime requestDate;

        private BigDecimal groomingFee;
        private List<MultipartFile> estimateImgList;
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
}
