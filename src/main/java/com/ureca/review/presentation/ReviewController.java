package com.ureca.review.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.review.application.ReviewService;
import com.ureca.review.domain.Enum.AuthorType;
import com.ureca.review.presentation.dto.ReviewDto;
import com.ureca.review.presentation.dto.ReviewLikeDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daenggu")
public class ReviewController {

    private final ReviewService reviewService;

    // 보호자 프로필 피드 조회 API
    @PostMapping("/customer/feed")
    public ResponseDto<List<ReviewDto.Response>> selectCustomerFeed(
            @RequestBody ReviewDto.Request request) {
        List<ReviewDto.Response> reviewList =
                reviewService.selectCustomerFeed(request.getCustomerId());
        return ResponseUtil.SUCCESS("피드 조회가 완료되었습니다.", reviewList);
    }

    // 디자이너 프로필 피드 조회 API
    @PostMapping("/designer/feed")
    public ResponseDto<List<ReviewDto.Response>> selectDesignerFeed(
            @RequestBody ReviewDto.Request request) {
        List<ReviewDto.Response> reviewList =
                reviewService.selectDesignerFeed(request.getDesignerId());
        return ResponseUtil.SUCCESS("피드 조회가 완료되었습니다.", reviewList);
    }

    // 보호자 피드 세부 조회
    @PostMapping("/feed/customer")
    public ResponseDto<ReviewDto.Response> selectCustomerFeedDetail(
            @RequestBody ReviewDto.Request request) {
        ReviewDto.Response reviewList =
                reviewService.selectCustomerFeedDetail(
                        request.getCustomerId(), request.getReviewId());
        return ResponseUtil.SUCCESS("피드 조회가 완료되었습니다.", reviewList);
    }

    // 디자이너 피드 세부 조회
    @PostMapping("/feed/designer")
    public ResponseDto<ReviewDto.Response> selectDesignerFeedDetail(
            @RequestBody ReviewDto.Request request) {
        ReviewDto.Response review =
                reviewService.selectDesignerFeedDetail(
                        request.getDesignerId(), request.getReviewId());
        return ResponseUtil.SUCCESS("피드 조회가 완료되었습니다.", review);
    }

    // 피드 생성
    @PutMapping("/feed")
    public ResponseDto<Void> createReview(
            @RequestPart("reviewRequest") String reviewRequestJson, // JSON 데이터를 String으로 받음
            @RequestPart("FeedImgList") List<MultipartFile> feedImgList)
            throws JsonProcessingException { // Multipart 파일 리스트를 받음

        // reviewRequestJson을 객체로 변환
        ReviewDto.Request reviewRequest =
                new ObjectMapper().readValue(reviewRequestJson, ReviewDto.Request.class);

        reviewRequest = reviewRequest.toBuilder().FeedImgList(feedImgList).build();

        reviewService.createReview(reviewRequest);
        return ResponseUtil.SUCCESS("리뷰가 성공적으로 생성되었습니다.", null);
    }

    // 피드 수정
    @PatchMapping("/feed")
    public ResponseDto<String> updateReview(
            @RequestPart("reviewRequest") String reviewRequestJson, // JSON 데이터를 String으로 받음
            @RequestPart("FeedImgList") List<MultipartFile> feedImgList)
            throws JsonProcessingException {

        // reviewRequestJson을 객체로 변환
        ReviewDto.Request reviewRequest =
                new ObjectMapper().readValue(reviewRequestJson, ReviewDto.Request.class);
        reviewService.updateReview(reviewRequest.getCustomerId(), reviewRequest, feedImgList);
        return ResponseUtil.SUCCESS("완료되었습니다.", null);
    }

    // 피드 삭제
    @DeleteMapping("/feed")
    public ResponseDto<String> deleteReview(@RequestParam Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseUtil.SUCCESS("완료되었습니다.", null);
    }

    // 피드탭 피드 세부 조회
    @PostMapping("/feed")
    public ResponseDto<List<ReviewDto.Response>> getFeed(
            @RequestBody(required = false) ReviewDto.Request request) {
        int size = 20;
        // request.getLastCreatedAt()을 사용하여 피드 조회
        List<ReviewDto.Response> feeds =
                reviewService.getFeeds(
                        request.getLastCreatedAt(),
                        size,
                        request.getUserId(),
                        request.getUserType());
        return ResponseUtil.SUCCESS("완료되었습니다.", feeds);
    }

    // 피드 좋아요
    @PostMapping("feed/like")
    public ResponseDto<ReviewDto.Response> getFeedLike(
            @RequestBody ReviewLikeDto.Request reviewLikeRequest) {
        ReviewDto.Response reviewResponse =
                reviewService.likeReview(
                        reviewLikeRequest.getReviewId(),
                        reviewLikeRequest.getUserId(),
                        AuthorType.valueOf(reviewLikeRequest.getUserType().toUpperCase()));
        return ResponseUtil.SUCCESS("좋아요 완료되었습니다.", reviewResponse);
    }
}
