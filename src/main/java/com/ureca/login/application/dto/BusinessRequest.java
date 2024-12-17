package com.ureca.login.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Data;

// 사업자 인증 API 요청 DTO
@Data
@Builder
public class BusinessRequest {

    private List<Business> businesses;

    @Data
    @Builder
    public static class Business {

        @JsonProperty("b_no")
        private String bNo;

        @JsonProperty("start_dt")
        private String startDt;

        @JsonProperty("p_nm")
        private String pNm;

        @JsonProperty("p_nm2")
        private String pNm2;

        @JsonProperty("b_nm")
        private String bNm;

        @JsonProperty("corp_no")
        private String corpNo;

        @JsonProperty("b_sector")
        private String bSector;

        @JsonProperty("b_type")
        private String bType;

        @JsonProperty("b_adr")
        private String bAdr;
    }
}
