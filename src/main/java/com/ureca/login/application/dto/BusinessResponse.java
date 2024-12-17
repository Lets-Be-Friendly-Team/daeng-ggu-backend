package com.ureca.login.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

// 사업자 인증 API 응답 DTO
public class BusinessResponse {

    @JsonProperty("data")
    private List<BusinessData> data;

    // Getter
    public List<BusinessData> getData() {
        return data;
    }

    public static class BusinessData {
        @JsonProperty("valid")
        private String valid;

        // Getter
        public String getValid() {
            return valid;
        }

        public void setValid(String valid) {
            this.valid = valid;
        }
    }
}
