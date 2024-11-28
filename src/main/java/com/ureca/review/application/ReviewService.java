package com.ureca.review.application;

import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.config.Redis.RedisLockUtil;
import com.ureca.global.application.S3Service;
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
        ReviewLike reviewLike = reviewLikeRepository.findByReview(review);
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
        ReviewLike reviewLike = reviewLikeRepository.findByReview(review);
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

    public void createReview(Long customerId, ReviewDto.Request reviewRequest) {
        Customer customer =
                (Customer) customerRepository
                        .findByCustomerId(customerId)
                        .orElseThrow(() -> new ApiException(ErrorCode.CUSTOMER_NOT_EXIST));

        Review review =
                Review.builder()
                        .reviewContents(reviewRequest.getReviewContents())
                        .reviewStar(reviewRequest.getReviewStar())
                        .isFeedAdd(reviewRequest.getIsFeedAdd())
                        .customer(customer)
                        .build();

        List<ReviewImage> reviewImages = new ArrayList<>();
        if (reviewRequest.getFeedImgList() != null) {
            for (MultipartFile file : reviewRequest.getFeedImgList()) {
                String imageUrl = s3Service.uploadFileImage(file, "review");
                ReviewImage reviewImage =
                        ReviewImage.builder().reviewImageUrl(imageUrl).review(review).build();
                reviewImages.add(reviewImage);
            }
        }

        reviewRepository.save(review);
        reviewImageRepository.saveAll(reviewImages);
    }

    public void updateReview(Long customerId, ReviewDto.Request reviewRequest) {
        Review review =
                reviewRepository
                        .findById(reviewRequest.getReviewId())
                        .orElseThrow(() -> new ApiException(ErrorCode.REVIEW_NOT_EXIST));

        review.updateReviewContents(review.getReviewContents());
        review.updateReviewStar(review.getReviewStar());

        // 이미지 업데이트
        List<ReviewImage> reviewImages = reviewImageRepository.findByReview(review);
        if (reviewRequest.getFeedImgList() != null) {
            for (MultipartFile file : reviewRequest.getFeedImgList()) {
                String imageUrl = s3Service.uploadFileImage(file, "review");
                ReviewImage reviewImage =
                        ReviewImage.builder().reviewImageUrl(imageUrl).review(review).build();
                reviewImages.add(reviewImage);
            }
        }

        reviewRepository.save(review);
        reviewImageRepository.saveAll(reviewImages);
    }

    public void deleteReview(Long reviewId) {
        Review review =
                reviewRepository
                        .findById(reviewId)
                        .orElseThrow(() -> new ApiException(ErrorCode.REVIEW_NOT_EXIST));

        reviewRepository.delete(review);
    }

    public List<ReviewDto.Response> getFeeds(LocalDateTime lastCreatedAt, int size) {
        List<Review> reviews = new ArrayList<>();
        if (lastCreatedAt == null) {
            reviews = reviewRepository.findTopN(size); // 첫 요청
        } else {
            Pageable pageable = PageRequest.of(0, size, Sort.by("createdAt").descending());
            reviews = reviewRepository.findByCreatedAtBefore(lastCreatedAt, pageable); // 이후 요청
        }

        List<ReviewDto.Response> reviewList = new ArrayList<>();
        for (Review review : reviews) {
            // review를 기반으로 ReviewImage 리스트 생성
            List<ReviewImage> reviewImages = reviewImageRepository.findByReview(review);
            ReviewLike reviewLike = reviewLikeRepository.findByReview(review);
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
                reviewLikeRepository.findByReviewAndUserIdAndUserType(
                        review, userId, userType);
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
        ReviewDto.Response response = ReviewDto.Response.builder()
                .reviewLikeCnt(review.getReviewLikeCnt())
                .isReviewLike(reviewLike.getIsReviewLike())
                .build();
        return response;
    }

    /*

    public void createReview(ReviewDto.Request requestDto,Long userId, List<MultipartFile> images) {
        // User 조회
        Customer user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        // Designer 조회
        Designer designer = designerRepository.findById(requestDto.getDesigner_id().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid designer ID"));


        // Review 생성
        Review review = Review.builder()
                .reviewStar(requestDto.getScore())
                .reviewContents(requestDto.getDesc())
                .is_feedAdd(requestDto.getFeed())
                .customer(user)
                .designer(designer)
                .build();

        List<ReviewImage> reviewImages = new ArrayList<>();
        for (MultipartFile image : images) {
            String imageUrl = S3Service.uploadFileImage(image, "review");
            if(review.getFeed() == true && review.getFeedurl() == null){review.setFeedurl(imageUrl);}
            ReviewImage reviewImage = ReviewImage.builder()
                    .imageUrl(imageUrl)
                    .review(review) // Review와 연관 설정
                    .build();

            reviewImages.add(reviewImage);
        }

        // Review 및 ReviewImage 저장
        reviewRepository.save(review); // 먼저 Review 저장
        reviewImageRepository.saveAll(reviewImages); // ReviewImage들 저장
    }

    public List<ReviewDto.Response> selectDesignerFeed(Long userId, Long designerId) {
        Designer designer = designerRepository.findById(designerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid designer ID"));

        // 미용사 리뷰 조회 로직
        List<Review> reviews = reviewRepository.findByDesigner(designer);
        // Review와 ReviewImage 리스트를 매핑
        List<ReviewDto.Response> responseList = new ArrayList<>();
        for (Review review : reviews) {
            // DTO로 변환하여 리스트에 추가
            ReviewDto.Response response = ReviewDto.Response.builder()
                    .id(review.getId())
                    .score(review.getScore())
                    .desc(review.getDesc())
                    .feedurl(review.getFeedurl())
                    .build();

            responseList.add(response);
        }

        return responseList;
    }

    public List<ReviewDto.Response> selectDesignerReview(Long userId, Long designerId) {
        Designer designer = designerRepository.findById(designerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid designer ID"));

        // 미용사 리뷰 조회 로직
        List<Review> reviews = reviewRepository.findByDesigner(designer);
        // Review와 ReviewImage 리스트를 매핑
        List<ReviewDto.Response> responseList = new ArrayList<>();
        for (Review review : reviews) {
            List<ReviewImage> reviewImages = reviewImageRepository.findByReview(review);

            // DTO로 변환하여 리스트에 추가
            ReviewDto.Response response = ReviewDto.Response.builder()
                    .id(review.getId())
                    .score(review.getScore())
                    .desc(review.getDesc())
                    .reviewImage(reviewImages)
                    .build();

            responseList.add(response);
        }

        return responseList;
    }

    public void deleteReview(Long userId, Long reviewId) {
        // 리뷰가 존재하는지 확인
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + reviewId));

        // 리뷰 작성자 확인
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to delete this review");
        }

        // 리뷰 삭제
        reviewRepository.delete(review);
    }

    public List<ReviewDto.Response> selectReviewFeed(Long userId, Long reviewId) {
        // 전체 리뷰 중에 feed 가 true 인 모든 리뷰를 조회
        List<Review> reviews = reviewRepository.findByFeedTrue();

        // Review와 ReviewImage 리스트를 매핑
        List<ReviewDto.Response> responseList = new ArrayList<>();
        for (Review review : reviews) {
            List<ReviewImage> reviewImages = reviewImageRepository.findByReview(review);

            // DTO로 변환하여 리스트에 추가
            ReviewDto.Response response = ReviewDto.Response.builder()
                    .id(review.getId())
                    .score(review.getScore())
                    .desc(review.getDesc())
                    .reviewImage(reviewImages)
                    .build();

            responseList.add(response);
        }

        return responseList;
    }


    public List<ReviewDto.Response> selectUserFeed(Long userid) {
        // 사용자 ID로 피드 참여 여부가 true인 리뷰를 조회
        List<Review> reviews = reviewRepository.findByUserIdAndFeedTrue(userid);

        // Review -> ReviewDto.Response로 변환
        return reviews.stream()
                .map(review -> ReviewDto.Response.builder()
                        .id(review.getId())
                        .feedurl(review.getFeedurl())
                        .build())
                .toList();
    }

    public ReviewDto.Response selectFeedDetail(Long reviewId) {
        // 리뷰 조회
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + reviewId));

        // review를 기반으로 ReviewImage 리스트 생성
        List<ReviewImage> reviewImages = reviewImageRepository.findByReview(review);

        // DTO로 매핑하여 반환
        return ReviewDto.Response.builder()
                .id(review.getId())
                .score(review.getScore())
                .desc(review.getDesc())
                .reviewImage(reviewImages)
                .build();
    }

    public List<ReviewDto.Response> selectUserFeedDetail(Long userId) {

        List<Review> reviews = reviewRepository.findByUserIdAndFeedTrue(userId);

        // Review와 ReviewImage 리스트를 매핑
        List<ReviewDto.Response> responseList = new ArrayList<>();
        for (Review review : reviews) {
            List<ReviewImage> reviewImages = reviewImageRepository.findByReview(review);

            // DTO로 변환하여 리스트에 추가
            ReviewDto.Response response = ReviewDto.Response.builder()
                    .id(review.getId())
                    .score(review.getScore())
                    .desc(review.getDesc())
                    .reviewImage(reviewImages)
                    .build();

            responseList.add(response);
        }

        return responseList;
    }

    public List<ReviewDto.Response> getFeeds(LocalDateTime lastCreatedAt, int size) {
        if (lastCreatedAt == null) {
            return reviewRepository.findTopN(size); // 첫 요청
        } else {
            Pageable pageable = PageRequest.of(0, size, Sort.by("createdAt").descending());
            return reviewRepository.findByCreatedAtBefore(lastCreatedAt, pageable); // 이후 요청
        }
    }  */
}