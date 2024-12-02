package com.ureca.estimate.presentation.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
@Getter
public class EstimateDtoDetail {
    private Long estimateId;
    private Long designerId;
    private String designerName;
    private String designerImageUrl;
    private String estimateDetail;
    private BigDecimal estimatePrice;
    private Long petId;
    private String petName;
    private LocalDateTime createdAt;
    private Long customerId;
    private String customerName;
    private String phone;
    private String address;
    private BigDecimal groomingFee;
    private List<String> estimateImgList;
}
