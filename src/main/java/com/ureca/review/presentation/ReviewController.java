package com.ureca.review.presentation;

import com.ureca.common.application.AuthService;
import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.review.application.ReviewService;
import com.ureca.review.domain.Enum.AuthorType;
import com.ureca.review.presentation.dto.ReviewDto;
import com.ureca.review.presentation.dto.ReviewLikeDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daengggu")
public class ReviewController {

    private final ReviewService reviewService;
    private final AuthService authService;

    // 보호자 프로필 피드 조회 API
    @PostMapping("/customer/feed")
    public ResponseDto<List<ReviewDto.Response>> selectCustomerFeed(
            @RequestBody ReviewDto.Request requestDto,
            HttpServletRequest request,
            HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        List<ReviewDto.Response> reviewList = reviewService.selectCustomerFeed(id);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS("피드 조회가 완료되었습니다.", reviewList);
    }

    // 디자이너 프로필 피드 조회 API
    @PostMapping("/designer/feed")
    public ResponseDto<List<ReviewDto.Response>> selectDesignerFeed(
            @RequestBody ReviewDto.Request requestDto,
            HttpServletRequest request,
            HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        List<ReviewDto.Response> reviewList =
                reviewService.selectDesignerFeed(requestDto.getDesignerId());
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS("피드 조회가 완료되었습니다.", reviewList);
    }

    // 보호자 피드 세부 조회
    @PostMapping("/feed/customer")
    @Operation(summary = "보호자 피드 세부 조회", description = "[FED1100] 보호자 프로필 조회- 피드 세부 조회")
    public ResponseDto<ReviewDto.Customer> selectCustomerFeedDetail(
            @RequestBody ReviewDto.ID reviewId,
            HttpServletRequest request,
            HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        ReviewDto.Customer reviewList =
                reviewService.selectCustomerFeedDetail(reviewId.getReviewId());
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS("피드 조회가 완료되었습니다.", reviewList);
    }

    // 디자이너 피드 세부 조회
    @PostMapping("/feed/designer")
    @Operation(summary = "디자이너 피드 세부 조회", description = "[FED1200] 디자이너 프로필 조회- 피드 세부 조회")
    public ResponseDto<ReviewDto.Designer> selectDesignerFeedDetail(
            @RequestBody ReviewDto.ID reviewId,
            HttpServletRequest request,
            HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        ReviewDto.Designer review = reviewService.selectDesignerFeedDetail(reviewId.getReviewId());
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS("피드 조회가 완료되었습니다.", review);
    }

    // 피드 생성 API
    @PostMapping("/feed")
    @Operation(summary = "리뷰 생성", description = "[RSV2100] 리뷰(피드) 생성 API")
    public ResponseDto<Void> createReview(
            @RequestBody ReviewDto.Request reviewRequest,
            HttpServletRequest request,
            HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        reviewService.createReview(id, reviewRequest);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS("리뷰가 성공적으로 생성되었습니다.", null);
    }

    // 피드 수정
    @PatchMapping(
            value = "/feed",
            consumes = "application/json", // 클라이언트 요청이 JSON 형식이어야 함
            produces = "application/json" // 서버 응답이 JSON 형식임을 명시
            )
    @Operation(summary = "리뷰 수정", description = "[FED1110] 보호자 프로필 조회 - 리뷰 세부 조회 - 리뷰 수정 ")
    public ResponseDto<String> updateReview(
            @RequestBody ReviewDto.Patch reviewRequest,
            HttpServletRequest request,
            HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        reviewService.updateReview(reviewRequest);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS("완료되었습니다.", null);
    }

    // 피드 삭제
    @DeleteMapping("/feed")
    @Operation(summary = "리뷰 삭제", description = "[FED1100] 보호자 프로필 조회 - 리뷰 세부 조회 - 리뷰 삭제 ")
    public ResponseDto<String> deleteReview(
            @RequestParam Long reviewId, HttpServletRequest request, HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        reviewService.deleteReview(reviewId);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS("완료되었습니다.", null);
    }

    // 피드탭 피드 세부 조회
    @GetMapping("/feed")
    @Operation(summary = "피드탭 피드 조회", description = "[FED1000] 피드 탭에서 전체 피드 10개씩 조회")
    public ResponseDto<ReviewDto.Like> getFeed(
            @RequestParam int page, HttpServletRequest request, HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        String role = authService.getRequestToRole(request);
        AuthorType authorType =
                "C".equalsIgnoreCase(role) ? AuthorType.CUSTOMER : AuthorType.DESIGNER;

        int size = 20;
        // request.getLastCreatedAt()을 사용하여 피드 조회
        ReviewDto.Like feeds = reviewService.getFeeds(page, size, id, authorType);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS("완료되었습니다.", feeds);
    }

    // 피드 좋아요
    @PostMapping("feed/like")
    @Operation(summary = "리뷰 좋아요", description = "[FED1000] 리뷰 세부 조회 - 리뷰 좋아요")
    public ResponseDto<ReviewLikeDto.Response> getFeedLike(
            @RequestBody ReviewLikeDto.Request reviewLikeRequest,
            HttpServletRequest request,
            HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        String role = authService.getRequestToRole(request);
        AuthorType authorType =
                "C".equalsIgnoreCase(role) ? AuthorType.CUSTOMER : AuthorType.DESIGNER;
        ReviewLikeDto.Response reviewResponse =
                reviewService.likeReview(reviewLikeRequest.getReviewId(), id, authorType);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS("좋아요 완료되었습니다.", reviewResponse);
    }
}
