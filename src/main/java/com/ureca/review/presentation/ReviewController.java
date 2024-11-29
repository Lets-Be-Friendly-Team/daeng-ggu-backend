package com.ureca.review.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.review.application.ReviewService;
import com.ureca.review.domain.Enum.AuthorType;
import com.ureca.review.presentation.dto.ReviewDto;
import java.time.LocalDateTime;
import java.util.List;

import com.ureca.review.presentation.dto.ReviewLikeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daenggu")
public class ReviewController {

    private final ReviewService reviewService;

    //보호자 프로필 피드 조회 API
    @PostMapping("/customer/feed")
    public ResponseDto<List<ReviewDto.Response>> selectCustomerFeed(@RequestBody ReviewDto.Request request) {
        List<ReviewDto.Response> reviewList = reviewService.selectCustomerFeed(request.getCustomerId());
        return ResponseUtil.SUCCESS("피드 조회가 완료되었습니다.",reviewList);
    }

    //디자이너 프로필 피드 조회 API
    @PostMapping("/designer/feed")
    public ResponseDto<List<ReviewDto.Response>> selectDesignerFeed(@RequestBody ReviewDto.Request request) {
        List<ReviewDto.Response> reviewList = reviewService.selectDesignerFeed(request.getDesignerId());
        return ResponseUtil.SUCCESS("피드 조회가 완료되었습니다.",reviewList);
    }

    //보호자 피드 세부 조회
    @PostMapping("/feed/customer")
    public ResponseDto<ReviewDto.Response> selectCustomerFeedDetail(@RequestBody ReviewDto.Request request) {
        ReviewDto.Response reviewList =
                reviewService.selectCustomerFeedDetail(request.getCustomerId(), request.getReviewId());
        return ResponseUtil.SUCCESS("피드 조회가 완료되었습니다.",reviewList);
    }

    //디자이너 피드 세부 조회
    @PostMapping("/feed/designer")
    public ResponseDto<ReviewDto.Response> selectDesignerFeedDetail(@RequestBody ReviewDto.Request request) {
        ReviewDto.Response review = reviewService.selectDesignerFeedDetail(request.getDesignerId(), request.getReviewId());
        return ResponseUtil.SUCCESS("피드 조회가 완료되었습니다.",review);
    }
}