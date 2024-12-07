package com.ureca.profile.application;

import com.ureca.common.application.S3Service;
import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.profile.domain.Breeds;
import com.ureca.profile.domain.Certificate;
import com.ureca.profile.domain.Designer;
import com.ureca.profile.domain.Portfolio;
import com.ureca.profile.domain.PortfolioImg;
import com.ureca.profile.domain.Services;
import com.ureca.profile.infrastructure.CertificateRepository;
import com.ureca.profile.infrastructure.DesignerRepository;
import com.ureca.profile.infrastructure.PortfolioImgRepository;
import com.ureca.profile.infrastructure.PortfolioRepository;
import com.ureca.profile.infrastructure.ServicesRepository;
import com.ureca.profile.presentation.dto.BreedCode;
import com.ureca.profile.presentation.dto.DesignerDetail;
import com.ureca.profile.presentation.dto.DesignerProfile;
import com.ureca.profile.presentation.dto.DesignerUpdate;
import com.ureca.profile.presentation.dto.PortfolioDetail;
import com.ureca.profile.presentation.dto.PortfolioInfo;
import com.ureca.profile.presentation.dto.PortfolioUpdate;
import com.ureca.profile.presentation.dto.ReviewInfo;
import com.ureca.review.domain.Review;
import com.ureca.review.domain.ReviewImage;
import com.ureca.review.infrastructure.ReviewRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DesignerService {

    private static final Logger logger = LoggerFactory.getLogger(DesignerService.class);

    @Autowired private DesignerRepository designerRepository;
    @Autowired private CertificateRepository certificateRepository;
    @Autowired private PortfolioRepository portfolioRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private PortfolioImgRepository imgRepository;
    @Autowired private ServicesRepository servicesRepository;
    @Autowired private S3Service s3Service;

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
        if (designerProfile == null) throw new ApiException(ErrorCode.DESIGNER_NOT_EXIST);
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
                                    if (reviewImages != null && !reviewImages.isEmpty()) {
                                        if (reviewImages.size() > 0)
                                            reviewInfo.setReviewImgUrl(
                                                    reviewImages.get(0).getReviewImageUrl());
                                    } else { // 이미지가 없는 경우 기본값 설정 (null 처리)
                                        reviewInfo.setReviewImgUrl(null);
                                    }
                                    return reviewInfo;
                                })
                        .collect(Collectors.toList());
        designerProfile.setReviewList(reviewList);

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
     * @title 디자이너 - 프로필 등록/수정
     * @description 디자이너 프로필 등록/수정
     * @param data 입력 정보
     */
    @Transactional
    public void updateDesignerProfile(DesignerUpdate data) {
        // 신규 등록
        if (data.getDesignerId() == null || data.getDesignerId() == 0) {
            // TODO 로그인 이후 추가할 데이터
            String designerLoginId = "test@navaer.com";
            String email = "test@navaer.com";
            String password = "1234";
            String role = "designer";
            String billingCode = "";
            String isMonthlyPay = "N";
            // TODO 좌표 변환 API 이후 좌표값
            double xPosition = 0.0;
            double yPosition = 0.0;

            // 이미지 등록
            String imageUrl = "", fileName = "";
            if (data.getNewImgFile() != null
                    && !data.getNewImgFile().getOriginalFilename().isEmpty()) {
                imageUrl =
                        s3Service.uploadFileImage(
                                data.getNewImgFile(),
                                "profile",
                                "designerProfile"); // TODO 파일명 짓는 양식 정하기
                fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
            }

            // 디자이너 등록
            List<Services> services = new ArrayList<>();
            if (data.getProvidedServices() != null) {
                for (String providedService : data.getProvidedServices()) { // 제공 서비스 코드 목록
                    Services newServices =
                            Services.builder().providedServicesCode(providedService).build();
                    services.add(newServices);
                }
            }
            List<Breeds> breeds = new ArrayList<>();
            if (data.getPossibleBreed() != null) {
                for (BreedCode breedCode : data.getPossibleBreed()) { // 미용 가능 견종 코드
                    Breeds newBreeds =
                            Breeds.builder()
                                    .possibleMajorBreedCode(breedCode.getMajorBreedCode())
                                    .possibleSubBreedCode(breedCode.getSubBreedCode())
                                    .build();
                    breeds.add(newBreeds);
                }
            }
            Designer newDesigner =
                    Designer.builder()
                            .designerLoginId(designerLoginId)
                            .email(email)
                            .password(password)
                            .role(role)
                            .designerName(data.getDesignerName())
                            .officialName(data.getNickname())
                            .phone(data.getPhone())
                            .billingCode(billingCode)
                            .isMonthlyPay(isMonthlyPay)
                            .monthlyPayDate(LocalDateTime.now())
                            .designerImgUrl(imageUrl)
                            .designerImgName(fileName)
                            .address1(data.getAddress1())
                            .address2(data.getAddress2())
                            .detailAddress(data.getDetailAddress())
                            .xPosition(xPosition)
                            .yPosition(yPosition)
                            .introduction(data.getIntroduction())
                            .workExperience(data.getWorkExperience())
                            .isVerified(data.getIsVerified())
                            .businessNumber(data.getBusinessNumber())
                            .businessIsVerified(data.getBusinessIsVerified())
                            .services(services)
                            .breeds(breeds)
                            .createdAt(LocalDateTime.now())
                            .build();
            Designer savedDesigner = designerRepository.save(newDesigner);

            // 인증서 등록
            if (data.getCertificationsFileList() != null
                    && !data.getCertificationsFileList().isEmpty()) {
                for (MultipartFile file : data.getCertificationsFileList()) {
                    String certificationsUrl =
                            s3Service.uploadFileImage(
                                    file, "profile", "certifications"); // TODO 파일명 짓는 양식 정하기
                    Certificate newCertificate =
                            Certificate.builder()
                                    .designer(savedDesigner)
                                    .imgUrl(certificationsUrl)
                                    .build();
                    certificateRepository.save(newCertificate);
                }
            }
        } else {
            // 기존 정보 조회
            Designer designer =
                    designerRepository
                            .findById(data.getDesignerId())
                            .orElseThrow(() -> new ApiException(ErrorCode.DESIGNER_NOT_EXIST));
            // 이미지 등록
            String imageUrl = data.getPreImgUrl();
            String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
            if (data.getNewImgFile() != null
                    && !data.getNewImgFile().getOriginalFilename().isEmpty()) {
                imageUrl =
                        s3Service.uploadFileImage(
                                data.getNewImgFile(),
                                "profile",
                                "designerProfile"); // TODO 파일명 짓는 양식 정하기
                fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
            }
            // 디자이너 등록
            List<Services> services = new ArrayList<>();
            for (String providedService : data.getProvidedServices()) { // 제공 서비스 코드 목록
                Optional<Services> existingServiceOpt =
                        servicesRepository.findByDesignerAndProvidedServicesCode(
                                designer, providedService);
                if (existingServiceOpt.isEmpty()) {
                    Services newServices =
                            Services.builder()
                                    .designer(designer)
                                    .providedServicesCode(providedService)
                                    .build();
                    services.add(newServices);
                } else {
                    Services existingService = existingServiceOpt.get();
                    services.add(existingService);
                }
            }
            List<Breeds> breeds = new ArrayList<>();
            if (data.getPossibleBreed() != null) {
                for (BreedCode breedCode : data.getPossibleBreed()) { // 미용 가능 견종 코드
                    Breeds newBreeds =
                            Breeds.builder()
                                    .designer(designer)
                                    .possibleMajorBreedCode(breedCode.getMajorBreedCode())
                                    .possibleSubBreedCode(breedCode.getSubBreedCode())
                                    .build();
                    breeds.add(newBreeds);
                }
            } else {
                breeds = designer.getBreeds();
            }
            Designer updateDesigner =
                    designer.toBuilder()
                            .designerId(data.getDesignerId())
                            .designerName(data.getDesignerName())
                            .officialName(data.getNickname())
                            .designerImgUrl(imageUrl)
                            .designerImgName(fileName)
                            .address1(data.getAddress1())
                            .address2(data.getAddress2())
                            .detailAddress(data.getDetailAddress())
                            .introduction(data.getIntroduction())
                            .phone(data.getPhone())
                            .isVerified(data.getIsVerified())
                            .businessNumber(data.getBusinessNumber())
                            .businessIsVerified(data.getBusinessIsVerified())
                            .services(services)
                            .breeds(breeds)
                            .updatedAt(LocalDateTime.now())
                            .build();
            // TODO 입력한 주소에 맞는 좌표값 세팅해줘야 된다.
            designerRepository.save(updateDesigner);

            // 인증서 등록
            List<String> preCertifications =
                    certificateRepository.findImgUrlsByDesignerId(
                            data.getDesignerId()); // 기존 인증서 url
            // 남은 인증서 url
            String[] certifications = data.getCertifications(); // 남은 인증서 url
            for (String certification : preCertifications) {
                boolean isPresent = false;
                for (String cert : certifications) {
                    if (certification.equals(cert)) {
                        isPresent = true;
                        break;
                    }
                }
                // 기존엔 있는데, 남은곳엔 없으면 preCertifications 에서 삭제.
                if (!isPresent) {
                    certificateRepository.deleteByDesignerDesignerIdAndImgUrl(
                            data.getDesignerId(), certification);
                }
            }
            // 신규 인증서 file
            if (data.getCertificationsFileList() != null
                    && !data.getCertificationsFileList().isEmpty()) {
                for (MultipartFile file : data.getCertificationsFileList()) {
                    String certificationsUrl =
                            s3Service.uploadFileImage(
                                    file, "profile", "certifications"); // TODO 파일명 짓는 양식 정하기
                    Certificate newCertificate =
                            Certificate.builder()
                                    .designer(designer)
                                    .imgUrl(certificationsUrl)
                                    .build();
                    certificateRepository.save(newCertificate);
                }
            }
        }
    } // updateDesignerProfile

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

    /**
     * @title 디자이너 - 포트폴리오 등록/수정
     * @description 포트폴리오 등록/수정
     * @param data 입력 정보
     * @return status 업데이트 성공 여부
     */
    @Transactional
    public void updateDesignerPortfolio(PortfolioUpdate data) {

        Designer designer =
                designerRepository
                        .findById(data.getDesignerId())
                        .orElseThrow(() -> new ApiException(ErrorCode.DESIGNER_NOT_EXIST));

        // 신규 등록
        if (data.getPortfolioId() == null || data.getPortfolioId() == 0) {

            // 신규 영상 등록
            String videoUrl = "", videoFileName = "";
            if (data.getNewVideoFile() != null
                    && !data.getNewVideoFile().getOriginalFilename().isEmpty()) {
                videoUrl =
                        s3Service.uploadFileImage(
                                data.getNewVideoFile(),
                                "portfolio",
                                "portfolio"); // TODO 파일명 짓는 양식 정하기
                videoFileName = videoUrl.substring(videoUrl.lastIndexOf('/') + 1);
            }

            // 입력 내용
            Portfolio newPortfolio =
                    Portfolio.builder()
                            .designer(designer)
                            .videoUrl(videoUrl)
                            .videoName(videoFileName)
                            .title(data.getTitle())
                            .contents(data.getContents())
                            .createdAt(LocalDateTime.now())
                            .build();
            // 등록
            Portfolio savedPortfolio = portfolioRepository.save(newPortfolio);

            // 신규 이미지 등록
            if (data.getNewImgFileList() != null && !data.getNewImgFileList().isEmpty()) {
                for (MultipartFile file : data.getNewImgFileList()) {
                    try {
                        String imgUrl =
                                s3Service.uploadFileImage(
                                        file, "portfolio", "portfolio"); // TODO 파일명 짓는 양식 정하기
                        String imgFileName = imgUrl.substring(imgUrl.lastIndexOf('/') + 1);

                        // 이미지 추가 내용
                        PortfolioImg newPortfolioImg =
                                PortfolioImg.builder()
                                        .portfolio(savedPortfolio)
                                        .imgUrl(imgUrl)
                                        .createdAt(LocalDateTime.now())
                                        .build();
                        // 등록
                        imgRepository.save(newPortfolioImg);

                    } catch (Exception e) {
                        new ApiException(ErrorCode.FILE_NOT_EXIST);
                    }
                }
            }

        } else {
            // 기존 정보 조회
            Portfolio portfolio =
                    portfolioRepository.findByDesignerDesignerIdAndPortfolioId(
                            data.getDesignerId(), data.getPortfolioId());
            if (portfolio == null) {
                throw new ApiException(ErrorCode.DATA_NOT_EXIST);
            }
            // 영상 등록
            String videoUrl = portfolio.getVideoUrl(), videoFileName = portfolio.getVideoName();
            if (data.getNewVideoFile() != null
                    && !data.getNewVideoFile().getOriginalFilename().isEmpty()) {
                videoUrl =
                        s3Service.updateFileImage(
                                data.getPreVideoUrl(),
                                data.getNewVideoFile()); // TODO 파일명 짓는 양식 정하기
                videoFileName = videoUrl.substring(videoUrl.lastIndexOf('/') + 1);
            }
            // 사진 등록
            if (data.getNewImgFileList() != null && !data.getNewImgFileList().isEmpty()) {
                for (MultipartFile file : data.getNewImgFileList()) {
                    try {
                        String imgUrl =
                                s3Service.uploadFileImage(
                                        file, "portfolio", "portfolio"); // TODO 파일명 짓는 양식 정하기
                        String imgFileName = imgUrl.substring(imgUrl.lastIndexOf('/') + 1);

                        // 이미지 추가 내용
                        PortfolioImg newPortfolioImg =
                                PortfolioImg.builder()
                                        .portfolio(portfolio)
                                        .imgUrl(imgUrl)
                                        .createdAt(LocalDateTime.now())
                                        .build();
                        // 등록
                        imgRepository.save(newPortfolioImg);

                    } catch (Exception e) {
                        new ApiException(ErrorCode.FILE_NOT_EXIST);
                    }
                }
            }

            // 입력 내용
            Portfolio newPortfolio =
                    portfolio.toBuilder()
                            .designer(designer)
                            .videoUrl(videoUrl)
                            .videoName(videoFileName)
                            .title(data.getTitle())
                            .contents(data.getContents())
                            .createdAt(LocalDateTime.now())
                            .build();
            // 등록
            portfolioRepository.save(newPortfolio);
        }
    } // updateDesignerPortfolio

    /**
     * @title 디자이너 - 포트폴리오 삭제
     * @description 선택한 포트폴리오 삭제
     * @param designerId 디자이너 아이디
     * @param portfolioId 포트폴리오 아이디
     */
    public void deleteDesignerPortfolio(Long designerId, Long portfolioId) {
        // TODO 당연히 존재하는 값일텐데 굳이 한번 더 조회하고 삭제하는 것이 맞을지?
        //  delete는 데이터 없으면 알아서 아무일도 일어나지 않음.
        Portfolio portfolio =
                portfolioRepository.findByDesignerDesignerIdAndPortfolioId(designerId, portfolioId);
        if (portfolio == null) {
            throw new ApiException(ErrorCode.DATA_NOT_EXIST);
        } else {
            portfolioRepository.delete(portfolio);
        }
    } // deleteDesignerPortfolio
}
