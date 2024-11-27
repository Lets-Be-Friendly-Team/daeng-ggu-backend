package com.ureca.review.presentation;

import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.review.application.ReviewService;
import com.ureca.review.domain.Enum.AuthorType;
import com.ureca.review.presentation.dto.ReviewDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daengggu")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/customer/feed")
    public ResponseDto<List<ReviewDto.Response>> selectCustomerFeed(
            @RequestBody Long customerId) {
        List<ReviewDto.Response> reviewList = reviewService.selectCustomerFeed(customerId);
        return ResponseUtil.SUCCESS("피드 조회가 완료되었습니다.",reviewList);
    }

    @PostMapping("/designer/feed")
    public ResponseDto<List<ReviewDto.Response>> selectDesignerFeed(
            @RequestBody Long designerId) {
        List<ReviewDto.Response> reviewList = reviewService.selectDesignerFeed(designerId);
        return ResponseUtil.SUCCESS("피드 조회가 완료되었습니다.",reviewList);
    }

    @PostMapping("/feed/customer")
    public ResponseDto<ReviewDto.Response> selectCustomerFeedDetail(
            @RequestBody Long customerId, @RequestBody Long reviewId) {
        ReviewDto.Response reviewList =
                reviewService.selectCustomerFeedDetail(customerId, reviewId);
        return ResponseUtil.SUCCESS("피드 조회가 완료되었습니다.",reviewList);
    }

    @PostMapping("/feed/designer")
    public ResponseDto<ReviewDto.Response> selectDesignerFeedDetail(
            @RequestBody Long designerId, @RequestBody Long reviewId) {
        ReviewDto.Response review = reviewService.selectDesignerFeedDetail(designerId, reviewId);
        return ResponseUtil.SUCCESS("피드 조회가 완료되었습니다.",review);
    }

    @PostMapping("/feed")
    public ResponseDto<Void> createReview(
            @RequestBody Long customerId, @RequestBody ReviewDto.Request reviewRequest) {
        reviewService.createReview(customerId, reviewRequest);
        return ResponseUtil.SUCCESS("리뷰가 성공적으로 생성되었습니다.", null);
    }

    @PatchMapping("/feed")
    public ResponseDto<String> updateReview(
            @RequestBody Long customerId, @RequestBody ReviewDto.Request reviewRequest) {
        reviewService.updateReview(customerId, reviewRequest);
        return ResponseUtil.SUCCESS("완료되었습니다.",null);
    }

    @DeleteMapping("/feed")
    public ResponseDto<String> deleteReview(
            @RequestBody Long customerId, @RequestParam("reviewId") Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseUtil.SUCCESS("완료되었습니다.",null);
    }

    @GetMapping("/feed")
    public ResponseDto<List<ReviewDto.Response>> getFeed(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime lastCreatedAt) {
        int size = 20;
        List<ReviewDto.Response> feeds = reviewService.getFeeds(lastCreatedAt, size);
        return ResponseUtil.SUCCESS("완료되었습니다.",feeds);
    }

    @PostMapping("feed/like")
    public ResponseDto<ReviewDto.Response> getFeedLike(@RequestBody Long userId,@RequestBody String userType,@RequestBody Long reviewId){
        ReviewDto.Response reviewResponse = reviewService.likeReview(reviewId, userId, AuthorType.valueOf(userType.toUpperCase()));
        return ResponseUtil.SUCCESS("좋아요 완료되었습니다.",reviewResponse);
    }
    /*
    //리뷰 생성 API
    @PostMapping("/review")
    public ResponseEntity<String> createReview(@RequestPart ReviewDto.Request requestDto, @RequestBody Long userId,  @RequestPart List<MultipartFile> image) {
        reviewService.createReview(requestDto,userId,image);
        return ResponseEntity.status(HttpStatus.CREATED).body("Review created successfully");
    }

    //리뷰 삭제 API
    @DeleteMapping("/review")
    public ResponseEntity<String> deleteReview(@RequestBody Long userId, @RequestBody Long reviewId) {
        reviewService.deleteReview(userId, reviewId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Review deleted successfully");
    }

    //특정 미용사 피드 조회 API
    @GetMapping("/review/feed/designer")
    public ResponseEntity<List<ReviewDto.Response>> getDesignerReview(@RequestBody Long userId, @RequestBody Long desingerId) {
        List<ReviewDto.Response> responsedto = reviewService.selectDesignerReview(userId,desingerId);
        return ResponseEntity.ok(responsedto);
    }

    //피드 전체 조회 API(그냥 전체 최신순)
    @GetMapping("/review/feed")
    public ResponseEntity<List<ReviewDto.Response>> getAllFeedReview(@RequestBody Long userId, @RequestBody Long reviewId) {
        List<ReviewDto.Response> responsedto = reviewService.selectReviewFeed(userId,reviewId);
        return ResponseEntity.ok(responsedto);
    }

    //특정 사용자 프로필 피드 조회 API(프로필 사진만 보여주는거)
    @GetMapping("/review/feed/profile")
    public ResponseEntity<List<ReviewDto.Response>> getFeedReviewProfile(@RequestBody Long userid) {
        List<ReviewDto.Response> reviewImages = reviewService.selectUserFeed(userid);
        return ResponseEntity.ok(reviewImages);
    }

    //특정피드 세부조회 API(피드 하나 세부정보)
    @GetMapping("/review/feed/{reviewId}")
    public ResponseEntity<ReviewDto.Response> getFeedReview(@PathVariable Long reviewId, @RequestBody Long userId) {
        ReviewDto.Response reviewImages = reviewService.selectFeedDetail(reviewId);
        return ResponseEntity.ok(reviewImages);
    }

    //특정 사용자 피드 리스트 세부조회 API(사용자 피드 세부정보 리스트 전체 반환)
    @GetMapping("/review/feed/profile/{userId}")
    public ResponseEntity<List<ReviewDto.Response>> getUserFeedDetail(@PathVariable Long userId) {
        List<ReviewDto.Response> reviewImages = reviewService.selectUserFeedDetail(userId);
        return ResponseEntity.ok(reviewImages);
    }

    //특정 미용사 피드 리스트 세부조회 API
    @GetMapping("/review/feed/designer")
    public ResponseEntity<List<ReviewDto.Response>> getDesignerFeedDetail(@RequestBody Long userId, @RequestBody Long desingerId) {
        List<ReviewDto.Response> responsedto = reviewService.selectDesignerReview(userId,desingerId);
        return ResponseEntity.ok(responsedto);
    }

    @GetMapping("/feed")
    public ResponseEntity<List<Review>> getFeed(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastCreatedAt,
            @RequestParam(defaultValue = "20") int size) {
        List<Review> feeds = reviewService.getFeeds(lastCreatedAt, size);
        return ResponseEntity.ok(feeds);
    }
    */
}