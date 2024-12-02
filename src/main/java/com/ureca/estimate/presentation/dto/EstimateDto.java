package com.ureca.estimate.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

public class EstimateDto {
    @Builder(toBuilder = true)
    @Getter
    public static class Request {
        private Long estimateId;
        private Long requestId;
        private Long customerId;
        private Long designerId;
        private Long petId;
        private String requestDetail;
        private LocalDateTime requestDate;
        private BigDecimal groomingFee;
        private List<MultipartFile> estimateImgList;
    }

    @Builder(toBuilder = true)
    @Getter
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
        private Boolean isDelivery;
        private Boolean isMonitoringIncluded;
        private String additionalRequest;
        private LocalDateTime createdAt;
        private List<EstimateDtoDetail> estimateList;
    }
}
