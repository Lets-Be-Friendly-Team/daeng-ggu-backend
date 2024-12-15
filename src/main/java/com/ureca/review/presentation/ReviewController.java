package com.ureca.review.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.review.application.ReviewService;
import com.ureca.review.domain.Enum.AuthorType;
import com.ureca.review.presentation.dto.ReviewDto;
import com.ureca.review.presentation.dto.ReviewLikeDto;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daengggu")
public class ReviewController {

    private final ReviewService reviewService;

    // 보호자 프로필 피드 조회 API
    @PostMapping("/customer/feed")
    public ResponseDto<List<ReviewDto.Response>> selectCustomerFeed(
            @RequestBody ReviewDto.Request request) {
        List<ReviewDto.Response> reviewList = reviewService.selectCustomerFeed(2L);
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
    @Operation(summary = "보호자 피드 세부 조회", description = "[FED1100] 보호자 프로필 조회- 피드 세부 조회")
    public ResponseDto<ReviewDto.Customer> selectCustomerFeedDetail(
            @RequestBody ReviewDto.ID reviewId) {
        ReviewDto.Customer reviewList =
                reviewService.selectCustomerFeedDetail(reviewId.getReviewId());
        return ResponseUtil.SUCCESS("피드 조회가 완료되었습니다.", reviewList);
    }

    // 디자이너 피드 세부 조회
    @PostMapping("/feed/designer")
    @Operation(summary = "디자이너 피드 세부 조회", description = "[FED1200] 디자이너 프로필 조회- 피드 세부 조회")
    public ResponseDto<ReviewDto.Designer> selectDesignerFeedDetail(
            @RequestBody ReviewDto.ID reviewId) {
        ReviewDto.Designer review = reviewService.selectDesignerFeedDetail(reviewId.getReviewId());
        return ResponseUtil.SUCCESS("피드 조회가 완료되었습니다.", review);
    }

    // 피드 생성
    @PostMapping("/feed")
    @Operation(summary = "리뷰 생성", description = "[RSV2100] 리뷰(피드) 생성 API")
    public ResponseDto<Void> createReview(
            @RequestPart("reviewRequest") String reviewRequestJson, // JSON 데이터를 String으로 받음
            @RequestPart("FeedImgList") List<MultipartFile> feedImgList)
            throws JsonProcessingException { // Multipart 파일 리스트를 받음

        // reviewRequestJson을 객체로 변환
        ReviewDto.Request reviewRequest =
                new ObjectMapper().readValue(reviewRequestJson, ReviewDto.Request.class);

        reviewService.createReview(2L, reviewRequest, feedImgList);
        return ResponseUtil.SUCCESS("리뷰가 성공적으로 생성되었습니다.", null);
    }

    // 피드 수정
    @PatchMapping("/feed")
    @Operation(summary = "리뷰 수정", description = "[FED1110] 보호자 프로필 조회 - 리뷰 세부 조회 - 리뷰 수정 ")
    public ResponseDto<String> updateReview(
            @RequestPart("reviewRequest") String reviewRequestJson, // JSON 데이터를 String으로 받음
            @RequestPart("FeedImgList") List<MultipartFile> feedImgList)
            throws JsonProcessingException {

        // reviewRequestJson을 객체로 변환
        ReviewDto.Patch reviewRequest =
                new ObjectMapper().readValue(reviewRequestJson, ReviewDto.Patch.class);
        reviewService.updateReview(reviewRequest, feedImgList);
        return ResponseUtil.SUCCESS("완료되었습니다.", null);
    }

    // 피드 삭제
    @DeleteMapping("/feed")
    @Operation(summary = "리뷰 삭제", description = "[FED1100] 보호자 프로필 조회 - 리뷰 세부 조회 - 리뷰 삭제 ")
    public ResponseDto<String> deleteReview(@RequestParam Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseUtil.SUCCESS("완료되었습니다.", null);
    }

    // 피드탭 피드 세부 조회
    @GetMapping("/feed")
    @Operation(summary = "피드탭 피드 조회", description = "[FED1000] 피드 탭에서 전체 피드 10개씩 조회")
    public ResponseDto<ReviewDto.Like> getFeed(@RequestParam int page) {
        int size = 20;
        // request.getLastCreatedAt()을 사용하여 피드 조회
        ReviewDto.Like feeds =
                reviewService.getFeeds(page, size, 2L, AuthorType.valueOf("CUSTOMER"));
        return ResponseUtil.SUCCESS("완료되었습니다.", feeds);
    }

    // 피드 좋아요
    @PostMapping("feed/like")
    @Operation(summary = "리뷰 좋아요", description = "[FED1000] 리뷰 세부 조회 - 리뷰 좋아요")
    public ResponseDto<ReviewLikeDto.Response> getFeedLike(
            @RequestBody ReviewLikeDto.Request reviewLikeRequest) {
        ReviewLikeDto.Response reviewResponse =
                reviewService.likeReview(
                        reviewLikeRequest.getReviewId(),
                        reviewLikeRequest.getUserId(),
                        AuthorType.valueOf(reviewLikeRequest.getUserType().toUpperCase()));
        return ResponseUtil.SUCCESS("좋아요 완료되었습니다.", reviewResponse);
    }
}
