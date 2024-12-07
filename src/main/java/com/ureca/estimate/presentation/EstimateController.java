package com.ureca.estimate.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.estimate.application.EstimateService;
import com.ureca.estimate.presentation.dto.EstimateDto;
import com.ureca.estimate.presentation.dto.EstimateDtoDetail;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daengggu")
public class EstimateController {

    private final EstimateService estimateService;
    private final ObjectMapper objectMapper;

    @PutMapping("/bid/estimate")
    ResponseDto<String> makeEstimate(
            @RequestPart("estimateRequest") String estimateRequestJson,
            @RequestPart("estimateImgList") List<MultipartFile> estimateImgList,
            @RequestPart("estimateImgIdList") String estimateImgIdListJson)
            throws JsonProcessingException {
        EstimateDto.Request request =
                objectMapper.readValue(estimateRequestJson, EstimateDto.Request.class);
        // estimateImgIdListJson을 List<String>으로 변환
        JavaType listType =
                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class);
        List<String> estimateImgIdList = objectMapper.readValue(estimateImgIdListJson, listType);
        estimateService.makeEstimate(request, estimateImgList, estimateImgIdList);
        return ResponseUtil.SUCCESS("견적서 생성이 완료되었습니다.", null);
    }

    @PostMapping("/bid/estimate/customer")
    ResponseDto<List<EstimateDto.Response>> selectCustomerEstimate(
            @RequestBody EstimateDto.Request request) {
        List<EstimateDto.Response> responseList =
                estimateService.selectCustomerEstimate(request.getCustomerId());
        return ResponseUtil.SUCCESS("견적서 조회가 완료되었습니다.", responseList);
    }

    @GetMapping("/bid/estimate/designer")
    public ResponseDto<List<EstimateDto.Response>> getPreviousEstimatesByDesigner(
            @RequestParam("designerId") Long designerId) {
        List<EstimateDto.Response> responseList =
                estimateService.getPreviousEstimatesByDesigner(designerId);
        return ResponseUtil.SUCCESS("디자이너 이전 견적서 조회에 성공하였습니다.", responseList);
    }

    @PostMapping("/bid/estimate")
    public ResponseDto<EstimateDtoDetail> getEstimateDetail(
            @RequestBody EstimateDto.Request request) {
        EstimateDtoDetail response = estimateService.getEstimateDetail(request.getEstimateId());
        return ResponseUtil.SUCCESS("견적서 세부 조회에 성공하였습니다.    ", response);
    }
}
