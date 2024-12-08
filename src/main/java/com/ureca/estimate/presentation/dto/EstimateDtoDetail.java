package com.ureca.estimate.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class EstimateDtoDetail {
    private Long estimateId;
    private Long designerId;
    private String designerName;
    private String designerImageUrl;
    private String designerAddress;
    private String estimateDetail;
    private BigDecimal groomingFee;
    private BigDecimal deliveryFee;
    private BigDecimal monitoringFee;
    private BigDecimal estimatePrice;
    private Long petId;
    private String petName;
    private LocalDateTime createdAt;
    private Long customerId;
    private String customerName;
    private String phone;
    private String address;
    private List<String> estimateImgList;
}
