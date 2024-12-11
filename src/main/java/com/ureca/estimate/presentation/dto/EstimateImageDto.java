package com.ureca.estimate.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(name = "EstimateImageRequest")
    public static class Request {
        private String estimateTagId;
        private MultipartFile estimateImgUrl;
    }
}
