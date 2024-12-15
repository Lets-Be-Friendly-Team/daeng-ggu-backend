package com.ureca.profile.application;

import com.ureca.common.application.S3Service;
import com.ureca.profile.infrastructure.BookmarkRepository;
import com.ureca.profile.infrastructure.CertificateRepository;
import com.ureca.profile.infrastructure.CustomerRepository;
import com.ureca.profile.infrastructure.DesignerRepository;
import com.ureca.profile.infrastructure.PetRepository;
import com.ureca.profile.infrastructure.PortfolioImgRepository;
import com.ureca.profile.infrastructure.PortfolioRepository;
import com.ureca.profile.infrastructure.ServicesRepository;
import com.ureca.profile.presentation.dto.ReviewInfo;
import com.ureca.review.domain.Review;
import com.ureca.review.domain.ReviewImage;
import com.ureca.review.infrastructure.ReviewRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);

    @Autowired private DesignerRepository designerRepository;
    @Autowired private CertificateRepository certificateRepository;
    @Autowired private PortfolioRepository portfolioRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private PortfolioImgRepository imgRepository;
    @Autowired private ServicesRepository servicesRepository;
    @Autowired private S3Service s3Service;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private PetRepository petRepository;
    @Autowired private BookmarkRepository bookmarkRepository;

    /**
     * @title List<Review> -> List<ReviewInfo>
     * @description 조회한 리뷰 엔티티 내용 DTO에 맞춰서 매핑
     * @param reviews 엔티티 조회 결과 목록
     * @return List<ReviewInfo> 사용자별 매핑 완료된 리뷰 조회 목록
     */
    public List<ReviewInfo> reviewToReviewInfo(List<Review> reviews, String userType) {
        List<ReviewInfo> reviewList =
                reviews.stream()
                        .map(
                                review -> {
                                    ReviewInfo reviewInfo = new ReviewInfo();
                                    reviewInfo.setReviewId(review.getReviewId());
                                    /// 리뷰 이미지 처리: 이미지가 없는 경우 null로 처리
                                    List<ReviewImage> reviewImages = review.getReviewImages();
                                    if (reviewImages != null && !reviewImages.isEmpty()) {
                                        if (reviewImages.size() > 0)
                                            reviewInfo.setReviewImgUrl1(
                                                    reviewImages.get(0).getReviewImageUrl());
                                        if (reviewImages.size() > 1)
                                            reviewInfo.setReviewImgUrl1(
                                                    reviewImages.get(1).getReviewImageUrl());
                                        if (reviewImages.size() > 2)
                                            reviewInfo.setReviewImgUrl1(
                                                    reviewImages.get(2).getReviewImageUrl());
                                    } else { // 이미지가 없는 경우 기본값 설정 (null 처리)
                                        reviewInfo.setReviewImgUrl1(null);
                                        reviewInfo.setReviewImgUrl2(null);
                                        reviewInfo.setReviewImgUrl3(null);
                                    }

                                    switch (userType) {
                                        case ("C"):
                                            reviewInfo.setDesignerId(
                                                    review.getDesigner().getDesignerId());
                                            reviewInfo.setDesignerImgUrl(
                                                    review.getDesigner().getDesignerImgUrl());
                                            reviewInfo.setDesignerAddress(
                                                    review.getDesigner().getAddress1());
                                            reviewInfo.setDesignerName(
                                                    review.getDesigner().getDesignerName());
                                            reviewInfo.setOfficialName(
                                                    review.getDesigner().getOfficialName());
                                            break;
                                        case ("D"):
                                            reviewInfo.setCustomerId(
                                                    review.getCustomer().getCustomerId());
                                            reviewInfo.setCustomerImgUrl(
                                                    review.getCustomer().getCustomerImgUrl());
                                            reviewInfo.setCustomerName(
                                                    review.getCustomer().getCustomerName());
                                            reviewInfo.setNickname(
                                                    review.getCustomer().getNickname());
                                            break;
                                        case ("A"):
                                            reviewInfo.setDesignerId(
                                                    review.getDesigner().getDesignerId());
                                            reviewInfo.setDesignerImgUrl(
                                                    review.getDesigner().getDesignerImgUrl());
                                            reviewInfo.setDesignerAddress(
                                                    review.getDesigner().getAddress1());
                                            reviewInfo.setDesignerName(
                                                    review.getDesigner().getDesignerName());
                                            reviewInfo.setOfficialName(
                                                    review.getDesigner().getOfficialName());
                                            reviewInfo.setCustomerId(
                                                    review.getCustomer().getCustomerId());
                                            reviewInfo.setCustomerImgUrl(
                                                    review.getCustomer().getCustomerImgUrl());
                                            reviewInfo.setCustomerName(
                                                    review.getCustomer().getCustomerName());
                                            reviewInfo.setNickname(
                                                    review.getCustomer().getNickname());
                                            break;
                                    }
                                    reviewInfo.setReviewContents(review.getReviewContents());
                                    reviewInfo.setReviewStar(review.getReviewStar());
                                    reviewInfo.setReviewLikeCnt(
                                            review.getReviewLikeCnt().intValue());
                                    reviewInfo.setFeedExposure(review.getIsFeedAdd());
                                    return reviewInfo;
                                })
                        .collect(Collectors.toList());
        return reviewList;
    }
}
