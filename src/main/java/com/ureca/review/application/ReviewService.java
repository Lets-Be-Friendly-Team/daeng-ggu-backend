package com.ureca.review.application;

import com.ureca.common.application.S3Service;
import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.config.Redis.RedisLockUtil;
import com.ureca.profile.domain.Customer;
import com.ureca.profile.domain.Designer;
import com.ureca.profile.infrastructure.CustomerRepository;
import com.ureca.profile.infrastructure.DesignerRepository;
import com.ureca.review.domain.Enum.AuthorType;
import com.ureca.review.domain.Review;
import com.ureca.review.domain.ReviewImage;
import com.ureca.review.domain.ReviewLike;
import com.ureca.review.infrastructure.ReviewImageRepository;
import com.ureca.review.infrastructure.ReviewLikeRepository;
import com.ureca.review.infrastructure.ReviewRepository;
import com.ureca.review.presentation.dto.ReviewDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;
    private final DesignerRepository designerRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final S3Service s3Service;
    private final RedisLockUtil redisLockUtil;

    private static final String LOCK_KEY_PREFIX = "review:like:";

    public List<ReviewDto.Response> selectCustomerFeed(Long customerId) {

        Customer customer = customerRepository.findById(customerId).orElse(null);

        List<Review> reviews = reviewRepository.findByCustomerOrderByCreatedAtDesc(customer);

        List<ReviewDto.Response> reviewList = new ArrayList<>();
        for (Review review : reviews) {
            ReviewDto.Response response =
                    ReviewDto.Response.builder()
                            .reviewId(review.getReviewId())
                            .feedUrl(review.getFeedUrl())
                            .build();

            reviewList.add(response);
        }

        return reviewList;
    }

    public List<ReviewDto.Response> selectDesignerFeed(Long designerId) {
        Designer designer = designerRepository.findById(designerId).orElse(null);

        List<Review> reviews = reviewRepository.findByDesignerOrderByCreatedAtDesc(designer);
        List<ReviewDto.Response> reviewList = new ArrayList<>();
        for (Review review : reviews) {
            ReviewDto.Response response =
                    ReviewDto.Response.builder()
                            .reviewId(review.getReviewId())
                            .feedUrl(review.getFeedUrl())
                            .build();

            reviewList.add(response);
        }

        return reviewList;
    }

    public ReviewDto.Response selectCustomerFeedDetail(Long customerId, Long reviewId) {

        Customer customer = customerRepository.findById(customerId).orElse(null);

        Review review = reviewRepository.findById(reviewId).get();

        List<ReviewImage> reviewImages = reviewImageRepository.findByReview(review);
        ReviewLike reviewLike =
                reviewLikeRepository.findByReviewAndUserIdAndUserType(
                        review, customerId, AuthorType.CUSTOMER);
        ReviewDto.Response response =
                ReviewDto.Response.builder()
                        .reviewId(review.getReviewId())
                        .reviewImgUrl1(
                                reviewImages.size() > 0
                                        ? reviewImages.get(0).getReviewImageUrl()
                                        : null)
                        .reviewImgUrl2(
                                reviewImages.size() > 1
                                        ? reviewImages.get(1).getReviewImageUrl()
                                        : null)
                        .reviewImgUrl3(
                                reviewImages.size() > 2
                                        ? reviewImages.get(2).getReviewImageUrl()
                                        : null)
                        .designerId(review.getDesigner().getDesignerId())
                        .designerImgUrl(review.getDesigner().getDesignerImgUrl())
                        .designerName(review.getDesigner().getDesignerName())
                        .reviewContents(review.getReviewContents())
                        .reviewStar(review.getReviewStar())
                        .reviewLikeCnt(review.getReviewLikeCnt())
                        .isReviewLike(
                                reviewLike != null
                                        ? reviewLike.getIsReviewLike()
                                        : false) // ReviewLike가 없으면 false
                        .build();

        return response;
    }

    public ReviewDto.Response selectDesignerFeedDetail(Long designerId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId).get();

        List<ReviewImage> reviewImages = reviewImageRepository.findByReview(review);
        ReviewLike reviewLike =
                reviewLikeRepository.findByReviewAndUserIdAndUserType(
                        review, designerId, AuthorType.DESIGNER);
        ReviewDto.Response response =
                ReviewDto.Response.builder()
                        .reviewId(review.getReviewId())
                        .reviewImgUrl1(
                                reviewImages.size() > 0
                                        ? reviewImages.get(0).getReviewImageUrl()
                                        : null)
                        .reviewImgUrl2(
                                reviewImages.size() > 1
                                        ? reviewImages.get(1).getReviewImageUrl()
                                        : null)
                        .reviewImgUrl3(
                                reviewImages.size() > 2
                                        ? reviewImages.get(2).getReviewImageUrl()
                                        : null)
                        .customerId(review.getCustomer().getCustomerId())
                        .customerImgUrl(review.getCustomer().getCustomerImgUrl())
                        .customerName(review.getCustomer().getCustomerName())
                        .reviewContents(review.getReviewContents())
                        .reviewStar(review.getReviewStar())
                        .reviewLikeCnt(review.getReviewLikeCnt())
                        .isReviewLike(
                                reviewLike != null
                                        ? reviewLike.getIsReviewLike()
                                        : false) // ReviewLike가 없으면 false
                        .build();

        return response;
    }

    public void createReview(ReviewDto.Request reviewRequest) {
        Customer customer =
                (Customer)
                        customerRepository
                                .findByCustomerId(reviewRequest.getCustomerId())
                                .orElseThrow(() -> new ApiException(ErrorCode.CUSTOMER_NOT_EXIST));
        System.out.println("Designer ID: " + reviewRequest.getDesignerId());
        Designer designer =
                designerRepository
                        .findByDesignerId(reviewRequest.getDesignerId())
                        .orElseThrow(() -> new ApiException(ErrorCode.DESIGNER_NOT_EXIST));

        Review review = new Review();

        List<ReviewImage> reviewImages = new ArrayList<>();
        if (reviewRequest.getFeedImgList() != null) {
            for (MultipartFile file : reviewRequest.getFeedImgList()) {
                String imageUrl = s3Service.uploadFileImage(file, "review", "review");

                review =
                        Review.builder()
                                .customer(customer)
                                .designer(designer)
                                .reviewContents(reviewRequest.getReviewContents())
                                .reviewStar(reviewRequest.getReviewStar())
                                .isFeedAdd(reviewRequest.getIsFeedAdd())
                                .feedUrl(imageUrl)
                                .build();

                ReviewImage reviewImage =
                        ReviewImage.builder().reviewImageUrl(imageUrl).review(review).build();
                reviewImages.add(reviewImage);
            }
        }

        reviewRepository.save(review);
        reviewImageRepository.saveAll(reviewImages);
    }

    @Transactional
    public void updateReview(
            Long customerId, ReviewDto.Request reviewRequest, List<MultipartFile> FeedImgList) {
        Review review =
                reviewRepository
                        .findById(reviewRequest.getReviewId())
                        .orElseThrow(() -> new ApiException(ErrorCode.REVIEW_NOT_EXIST));

        List<String> keepImgUrls = reviewRequest.getExistImgList();

        // 3. DB에서 기존 리뷰 이미지 가져오기
        List<ReviewImage> existingImages = reviewImageRepository.findByReview(review);

        // 4. 기존 이미지 삭제 (남길 이미지 제외)
        for (ReviewImage image : existingImages) {
            if (!keepImgUrls.contains(image.getReviewImageUrl())) {
                s3Service.deleteFileImage(image.getReviewImageUrl()); // S3에서 삭제
                reviewImageRepository.delete(image); // DB에서 삭제
            }
        }

        // 5. 새로운 이미지 업로드
        if (FeedImgList != null && !FeedImgList.isEmpty()) {
            List<ReviewImage> newImages = new ArrayList<>();
            for (MultipartFile file : FeedImgList) {
                String imageUrl = s3Service.uploadFileImage(file, "review", "review");
                ReviewImage newImage =
                        ReviewImage.builder().reviewImageUrl(imageUrl).review(review).build();
                newImages.add(newImage);
            }
            reviewImageRepository.saveAll(newImages); // DB에 저장
        }

        List<ReviewImage> reviewImage = reviewImageRepository.findByReview(review);

        // 6. 리뷰 내용 업데이트
        review =
                review.toBuilder()
                        .reviewContents(reviewRequest.getReviewContents())
                        .reviewStar(reviewRequest.getReviewStar())
                        .isFeedAdd(reviewRequest.getIsFeedAdd())
                        .feedUrl(reviewImage.get(0).getReviewImageUrl())
                        .reviewImages(reviewImage)
                        .build();
        reviewRepository.save(review);
    }

    public void deleteReview(Long reviewId) {
        Review review =
                reviewRepository
                        .findById(reviewId)
                        .orElseThrow(() -> new ApiException(ErrorCode.REVIEW_NOT_EXIST));

        reviewRepository.delete(review);
    }

    public List<ReviewDto.Response> getFeeds(
            LocalDateTime lastCreatedAt, int size, Long userId, AuthorType userType) {
        List<Review> reviews = new ArrayList<>();
        if (lastCreatedAt == null) {
            // 첫 요청 시, 페이지당 size개의 리뷰 반환
            Page<Review> reviewPage =
                    reviewRepository.findByCreatedAtBeforeAndIsFeedAddTrue(
                            LocalDateTime.now(),
                            PageRequest.of(0, size, Sort.by("createdAt").descending()));
            reviews = reviewPage.getContent();
        } else {
            // 이후 요청 시, lastCreatedAt 기준으로 이전 리뷰 조회
            Pageable pageable = PageRequest.of(0, size, Sort.by("createdAt").descending());
            Page<Review> reviewPage =
                    reviewRepository.findByCreatedAtBeforeAndIsFeedAddTrue(lastCreatedAt, pageable);
            reviews = reviewPage.getContent();
        }

        List<ReviewDto.Response> reviewList = new ArrayList<>();
        for (Review review : reviews) {
            // review를 기반으로 ReviewImage 리스트 생성
            List<ReviewImage> reviewImages = reviewImageRepository.findByReview(review);
            List<String> feedImgList = new ArrayList<>();
            for (ReviewImage reviewImage : reviewImages) {
                feedImgList.add(reviewImage.getReviewImageUrl());
            }
            ReviewLike reviewLike =
                    reviewLikeRepository.findByReviewAndUserIdAndUserType(review, userId, userType);

            ReviewDto.Response response =
                    ReviewDto.Response.builder()
                            .reviewId(review.getReviewId())
                            .reviewImgUrl1(reviewImages.size() > 0 ? feedImgList.get(0) : null)
                            .reviewImgUrl2(reviewImages.size() > 1 ? feedImgList.get(1) : null)
                            .reviewImgUrl3(reviewImages.size() > 2 ? feedImgList.get(2) : null)
                            .lastCreatedAt(review.getCreatedAt())
                            .designerId(review.getDesigner().getDesignerId())
                            .designerImgUrl(review.getDesigner().getDesignerImgUrl())
                            .designerName(review.getDesigner().getDesignerName())
                            .customerId(review.getCustomer().getCustomerId())
                            .customerImgUrl(review.getCustomer().getCustomerImgUrl())
                            .customerName(review.getCustomer().getCustomerName())
                            .reviewContents(review.getReviewContents())
                            .reviewStar(review.getReviewStar())
                            .reviewLikeCnt(review.getReviewLikeCnt())
                            .isReviewLike(
                                    reviewLike != null
                                            ? reviewLike.getIsReviewLike()
                                            : false) // ReviewLike가 없으면 false
                            .FeedImgList(feedImgList)
                            .build();

            reviewList.add(response);
        }

        return reviewList;
    }

    @Transactional
    public ReviewDto.Response likeReview(Long reviewId, Long userId, AuthorType userType) {
        String lockKey = LOCK_KEY_PREFIX + reviewId;

        // 1. 락 시도 (유효 시간 5초)
        boolean lockAcquired = redisLockUtil.tryLock(lockKey, 5000);

        if (!lockAcquired) {
            throw new ApiException(ErrorCode.USER_CONFLICT_ERROR);
        }

        // 2. 리뷰 조회
        Review review =
                reviewRepository
                        .findById(reviewId)
                        .orElseThrow(() -> new ApiException(ErrorCode.REVIEW_NOT_EXIST));
        // 3. 리뷰 좋아요 엔티티 조회 (같은 사용자, 같은 리뷰에 대한 좋아요 존재 여부 체크)
        ReviewLike existingReviewLike =
                reviewLikeRepository.findByReviewAndUserIdAndUserType(review, userId, userType);
        ReviewLike reviewLike = new ReviewLike();
        try {

            if (existingReviewLike == null) {
                // 4. 좋아요 엔티티가 없다면 새로 생성
                reviewLike =
                        ReviewLike.builder()
                                .review(review)
                                .userId(userId)
                                .userType(userType)
                                .isReviewLike(true) // 처음 좋아요를 눌렀으므로 true
                                .build();
                reviewLikeRepository.save(reviewLike);

                // 5. 리뷰의 좋아요 수 증가
                review = review.increaseReviewLikeCnt();
            } else {
                // 6. 이미 좋아요를 눌렀다면 상태 변경
                boolean currentLikeStatus = existingReviewLike.getIsReviewLike();

                reviewLike =
                        ReviewLike.builder()
                                .reviewLikeId(existingReviewLike.getReviewLikeId())
                                .review(existingReviewLike.getReview())
                                .userId(existingReviewLike.getUserId())
                                .userType(existingReviewLike.getUserType())
                                .isReviewLike(!currentLikeStatus) // 좋아요 상태 반전
                                .build();
                reviewLikeRepository.save(reviewLike);

                // 7. 리뷰 좋아요 수 상태 변경 (좋아요 취소는 감소, 새로 좋아요는 증가)
                review =
                        currentLikeStatus
                                ? review.decreaseReviewLikeCnt() // 좋아요 취소
                                : review.increaseReviewLikeCnt(); // 좋아요 추가
            }

            // 8. 리뷰 정보 저장
            reviewRepository.save(review);
        } finally {
            // 9. 락 해제
            redisLockUtil.unlock(lockKey);
        }
        ReviewDto.Response response =
                ReviewDto.Response.builder()
                        .reviewLikeCnt(review.getReviewLikeCnt())
                        .isReviewLike(reviewLike.getIsReviewLike())
                        .build();
        return response;
    }
}