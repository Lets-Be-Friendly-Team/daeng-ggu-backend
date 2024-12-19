package com.ureca.reservation.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DesignerInfoDto {

    @JsonProperty("designerId")
    private Long designerId;

    private String designerName;
    private String officialName;
    private String designerImgUrl;
    private String designerImgName;
    private String address1;
    private String address2;
    private String detailAddress;
    private String introduction;
    private String workExperience;
    private String businessNumber;
    private String businessIsVerified;
}
