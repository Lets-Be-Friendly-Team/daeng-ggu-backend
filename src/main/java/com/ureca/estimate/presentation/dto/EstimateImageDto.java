package com.ureca.estimate.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

public class EstimateImageDto {
    @Builder(toBuilder = true)
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private String estimateTagId;
        private MultipartFile estimateImgUrl;
    }
}
