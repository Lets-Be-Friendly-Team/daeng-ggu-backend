package com.ureca.estimate.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class EstimateImageDto {
    @Builder(toBuilder = true)
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private String estimateTagId;
        private String estimateImgUrl;
    }
}
