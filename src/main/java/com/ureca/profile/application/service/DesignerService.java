package com.ureca.profile.application.service;

import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.profile.domain.Portfolio;
import com.ureca.profile.infrastructure.CertificateRepository;
import com.ureca.profile.infrastructure.DesignerRepository;
import com.ureca.profile.infrastructure.ImgRepository;
import com.ureca.profile.infrastructure.PortfolioRepository;
import com.ureca.profile.presentation.dto.DesignerDetail;
import com.ureca.profile.presentation.dto.DesignerProfile;
import com.ureca.profile.presentation.dto.PortfolioDetail;
import com.ureca.profile.presentation.dto.PortfolioInfo;
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
public class DesignerService {

    private static final Logger logger = LoggerFactory.getLogger(DesignerService.class);

    @Autowired private DesignerRepository designerRepository;
    @Autowired private CertificateRepository certificateRepository;
    @Autowired private PortfolioRepository portfolioRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private ImgRepository imgRepository;

    /**
     * @title 디자이너 - 프로필
     * @description 디자이너정보, 제공서비스, 가능견종, 포트폴리오목록, 리뷰목록 조회
     * @param designerId 디자이너 아이디
     * @return DesignerProfile 디자이너 프로필 정보
     */
    public DesignerProfile getDesignerProfile(Long designerId) {

        // 디자이너 정보
        DesignerProfile designerProfile =
                designerRepository.findDesignerProfileByDesignerId(designerId);
        // 별점 평균
        designerProfile.setReviewStarAvg(
                designerRepository.findAverageReviewStarByDesignerId(designerId));
        // 리뷰 전체 좋아요 수
        designerProfile.setReviewLikeCntAll(
                designerRepository.findTotalReviewLikeCountByDesignerId(designerId));
        // 제공 서비스
        designerProfile.setProvidedServices(
                designerRepository.findDesignerProvidedServices(designerId));
        // 미용 가능 견종
        designerProfile.setPossibleBreeds(designerRepository.findDesignerMajorBreeds(designerId));
        // 자격증 이미지 URL
        List<String> imgUrls = certificateRepository.findImgUrlsByDesignerId(designerId);
        designerProfile.setCertifications(imgUrls.toArray(new String[0]));

        // 포트폴리오 목록
        List<Portfolio> portfolios = portfolioRepository.findByDesignerDesignerId(designerId);
        List<PortfolioInfo> portfolioInfos =
                portfolios.stream()
                        .map(
                                portfolio -> {
                                    // 포트폴리오 기본 정보 설정
                                    PortfolioInfo portfolioInfo =
                                            new PortfolioInfo(
                                                    portfolio.getPortfolioId(),
                                                    portfolio.getTitle(),
                                                    portfolio.getVideoUrl(),
                                                    portfolio.getContents());
                                    // 포트폴리오 이미지 URL 목록 조회
                                    List<String> portfolioImgUrls =
                                            imgRepository.findImgUrlByPortfolioPortfolioId(
                                                    portfolio.getPortfolioId());
                                    // 빈 리스트 처리: 이미지 URL이 없을 경우 빈 배열 설정
                                    if (portfolioImgUrls == null || portfolioImgUrls.isEmpty()) {
                                        portfolioInfo.setImgUrlList(
                                                new String[0]); // 이미지가 없으면 빈 배열 설정
                                    } else {
                                        portfolioInfo.setImgUrlList(
                                                portfolioImgUrls.toArray(
                                                        new String[0])); // 이미지가 있으면 배열로 변환하여 설정
                                    }
                                    return portfolioInfo;
                                })
                        .collect(Collectors.toList()); // List<PortfolioInfo>로 반환
        designerProfile.setPortfolioList(portfolioInfos);

        // 리뷰 목록
        List<Review> reviews =
                reviewRepository.findByDesignerDesignerId(designerId); // 디자이너 아이디로 리뷰 조회
        List<ReviewInfo> reviewList =
                reviews.stream()
                        .map(
                                review -> {
                                    ReviewInfo reviewInfo = new ReviewInfo();
                                    reviewInfo.setReviewId(review.getReviewId());
                                    /// 리뷰 이미지 처리: 이미지가 없는 경우 null로 처리
                                    List<ReviewImage> reviewImages = review.getReviewImages();
                                    if (reviewImages != null
                                            && !reviewImages.isEmpty()) { // 이미지가 있다면 최대 3개까지 가져오기
                                        if (reviewImages.size() > 0)
                                            reviewInfo.setReviewImgUrl1(
                                                    reviewImages.get(0).getReviewImageUrl());
                                        if (reviewImages.size() > 1)
                                            reviewInfo.setReviewImgUrl2(
                                                    reviewImages.get(1).getReviewImageUrl());
                                        if (reviewImages.size() > 2)
                                            reviewInfo.setReviewImgUrl3(
                                                    reviewImages.get(2).getReviewImageUrl());
                                    } else { // 이미지가 없는 경우 기본값 설정 (null 처리)
                                        reviewInfo.setReviewImgUrl1(null);
                                        reviewInfo.setReviewImgUrl2(null);
                                        reviewInfo.setReviewImgUrl3(null);
                                    }
                                    reviewInfo.setDesignerId(review.getDesigner().getDesignerId());
                                    reviewInfo.setDesignerImgUrl(
                                            review.getDesigner().getDesignerImgUrl());
                                    reviewInfo.setDesignerAddress(
                                            review.getDesigner().getAddress1()); // TODO 잘라서 보낼지 정하기
                                    reviewInfo.setReviewContents(review.getReviewContents());
                                    reviewInfo.setReviewStar(review.getReviewStar());
                                    reviewInfo.setReviewLikeCnt(review.getReviewLikeCnt());
                                    return reviewInfo;
                                })
                        .collect(Collectors.toList());
        designerProfile.setReviewList(reviewList);

        // 응답 검증
        if (designerProfile == null) {
            throw new ApiException(ErrorCode.DATA_NOT_EXIST);
        } else {
            logger.info(String.valueOf(designerProfile));
        }

        return designerProfile;
    } // getDesignerProfile

    /**
     * @title 디자이너 - 프로필 상세
     * @description 디자이너정보, 제공서비스, 가능견종, 포트폴리오목록 조회
     * @param designerId 디자이너 아이디
     * @return DesignerDetail 디자이너 프로필 상세 정보
     */
    public DesignerDetail getDesignerDetail(Long designerId) {

        // 디자이너 정보
        DesignerDetail designerDetail =
                designerRepository.findDesignerDetailByDesignerId(designerId);
        // 제공 서비스
        designerDetail.setProvidedServices(
                designerRepository.findDesignerProvidedServices(designerId));
        // 미용 가능 견종
        designerDetail.setPossibleBreeds(designerRepository.findDesignerMajorBreeds(designerId));
        // 자격증 이미지 URL
        List<String> imgUrls = certificateRepository.findImgUrlsByDesignerId(designerId);
        designerDetail.setCertifications(imgUrls.toArray(new String[0]));

        // 포트폴리오 목록
        List<Portfolio> portfolios = portfolioRepository.findByDesignerDesignerId(designerId);
        List<PortfolioInfo> portfolioInfos =
                portfolios.stream()
                        .map(
                                portfolio -> {
                                    // 포트폴리오 기본 정보 설정
                                    PortfolioInfo portfolioInfo =
                                            new PortfolioInfo(
                                                    portfolio.getPortfolioId(),
                                                    portfolio.getTitle(),
                                                    portfolio.getVideoUrl(),
                                                    portfolio.getContents());
                                    // 포트폴리오 이미지 URL 목록 조회
                                    List<String> portfolioImgUrls =
                                            imgRepository.findImgUrlByPortfolioPortfolioId(
                                                    portfolio.getPortfolioId());
                                    // 빈 리스트 처리: 이미지 URL이 없을 경우 빈 배열 설정
                                    if (portfolioImgUrls == null || portfolioImgUrls.isEmpty()) {
                                        portfolioInfo.setImgUrlList(
                                                new String[0]); // 이미지가 없으면 빈 배열 설정
                                    } else {
                                        portfolioInfo.setImgUrlList(
                                                portfolioImgUrls.toArray(
                                                        new String[0])); // 이미지가 있으면 배열로 변환하여 설정
                                    }
                                    return portfolioInfo;
                                })
                        .collect(Collectors.toList()); // List<PortfolioInfo>로 반환
        designerDetail.setPortfolioList(portfolioInfos);

        // 응답 검증
        if (designerDetail == null) {
            throw new ApiException(ErrorCode.DATA_NOT_EXIST);
        } else {
            logger.info(String.valueOf(designerDetail));
        }

        return designerDetail;
    } // getDesignerDetail

    /**
     * @title 디자이너 - 포트폴리오 상세
     * @description 디자이너정보, 제공서비스, 가능견종, 포트폴리오목록 조회
     * @param designerId 디자이너 아이디
     * @param portfolioId 포트폴리오 아이디
     * @return PortfolioDetail 디자이너 포트폴리오 상세 정보
     */
    public PortfolioDetail getDesignerPortfolioDetail(Long designerId, Long portfolioId) {

        Portfolio portfolio =
                portfolioRepository.findByDesignerDesignerIdAndPortfolioId(designerId, portfolioId);
        if (portfolio == null) {
            throw new ApiException(ErrorCode.DATA_NOT_EXIST);
        }
        PortfolioDetail portfolioDetail = new PortfolioDetail();
        portfolioDetail.setDesignerId(portfolio.getDesigner().getDesignerId()); // 디자이너 ID
        portfolioDetail.setPortfolioId(portfolio.getPortfolioId()); // 포트폴리오 ID
        portfolioDetail.setTitle(portfolio.getTitle()); // 제목
        portfolioDetail.setVideoUrl(portfolio.getVideoUrl()); // 동영상 URL
        portfolioDetail.setVideoName(portfolio.getVideoName()); // 동영상명
        portfolioDetail.setImgUrlList(new String[] {}); // 이미지 URL 목록 (필요한 경우 추가 로직 필요)
        portfolioDetail.setContents(portfolio.getContents()); // 내용
        // 포트폴리오 이미지 URL 목록
        List<String> portfolioImgUrls =
                imgRepository.findImgUrlByPortfolioPortfolioId(portfolio.getPortfolioId());
        // 빈 리스트 처리: 이미지 URL이 없을 경우 빈 배열 설정
        if (portfolioImgUrls == null || portfolioImgUrls.isEmpty()) {
            portfolioDetail.setImgUrlList(new String[0]); // 이미지가 없으면 빈 배열 설정
        } else {
            portfolioDetail.setImgUrlList(
                    portfolioImgUrls.toArray(new String[0])); // 이미지가 있으면 배열로 변환하여 설정
        }
        // 응답 검증
        if (portfolioDetail == null) {
            throw new ApiException(ErrorCode.DATA_NOT_EXIST);
        } else {
            logger.info(String.valueOf(portfolioDetail));
        }

        return portfolioDetail;
    } // getDesignerPortfolioDetail
}
