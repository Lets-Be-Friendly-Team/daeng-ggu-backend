package com.ureca.review.application;

import com.ureca.common.application.S3Service;
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
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
