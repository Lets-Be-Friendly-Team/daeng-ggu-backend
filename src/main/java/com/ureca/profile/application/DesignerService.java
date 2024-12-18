package com.ureca.profile.application;

import com.ureca.common.application.S3Service;
import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.common.util.ValidationUtil;
import com.ureca.login.application.ExternalService;
import com.ureca.login.application.dto.Coordinate;
import com.ureca.login.presentation.dto.KakaoDTO;
import com.ureca.profile.domain.Breeds;
import com.ureca.profile.domain.Certificate;
import com.ureca.profile.domain.Designer;
import com.ureca.profile.domain.Portfolio;
import com.ureca.profile.domain.PortfolioImg;
import com.ureca.profile.domain.Price;
import com.ureca.profile.domain.Services;
import com.ureca.profile.infrastructure.BreedsRepository;
import com.ureca.profile.infrastructure.CertificateRepository;
import com.ureca.profile.infrastructure.DesignerRepository;
import com.ureca.profile.infrastructure.PortfolioImgRepository;
import com.ureca.profile.infrastructure.PortfolioRepository;
import com.ureca.profile.infrastructure.PriceRepository;
import com.ureca.profile.infrastructure.ServicesRepository;
import com.ureca.profile.presentation.dto.BreedPriceTime;
import com.ureca.profile.presentation.dto.DesignerDetail;
import com.ureca.profile.presentation.dto.DesignerProfile;
import com.ureca.profile.presentation.dto.DesignerRegister;
import com.ureca.profile.presentation.dto.DesignerSignup;
import com.ureca.profile.presentation.dto.DesignerUpdate;
import com.ureca.profile.presentation.dto.PortfolioDetail;
import com.ureca.profile.presentation.dto.PortfolioInfo;
import com.ureca.profile.presentation.dto.PortfolioInfoUrl;
import com.ureca.profile.presentation.dto.PortfolioUpdate;
import com.ureca.profile.presentation.dto.ProvidedServices;
import com.ureca.review.domain.Review;
import com.ureca.review.infrastructure.ReviewRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DesignerService {

    private static final Logger logger = LoggerFactory.getLogger(DesignerService.class);

    @Autowired private DesignerRepository designerRepository;
    @Autowired private CertificateRepository certificateRepository;
    @Autowired private PortfolioRepository portfolioRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private PortfolioImgRepository imgRepository;
    @Autowired private ServicesRepository servicesRepository;
    @Autowired private BreedsRepository breedsRepository;
    @Autowired private PriceRepository priceRepository;
    @Autowired private ExternalService externalService;
    @Autowired private ProfileService profileService;
    @Autowired private S3Service s3Service;

    private static final String DESIGNER = "D";
    private static final String REQUIRED_SERVICE = "S1";
    private static final String OPTIONAL_SERVICE1 = "S2";
    private static final String OPTIONAL_SERVICE2 = "S3";
    private static final String OPTIONAL_SERVICE3 = "S4";

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
        designerProfile.setReviewList(profileService.reviewToReviewInfo(reviews, DESIGNER));

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
     * @title 디자이너 - 프로필 수정
     * @description 디자이너 프로필 수정
     * @param data 입력 정보
     */
    @Transactional
    public void updateDesignerProfile(DesignerUpdate data) {
        // 기존 정보 조회
        Designer designer =
                designerRepository
                        .findById(data.getDesignerId())
                        .orElseThrow(() -> new ApiException(ErrorCode.DESIGNER_NOT_EXIST));
        String imageUrl = designer.getDesignerImgUrl();
        if (!data.getNewImgUrl().isEmpty()) { // 신규 이미지 업데이트
            imageUrl = data.getNewImgUrl();
        }
        // 제공 서비스 코드 목록
        if (data.getProvidedServices().contains(REQUIRED_SERVICE)
                && (data.getProvidedServices().contains(OPTIONAL_SERVICE1)
                        || data.getProvidedServices().contains(OPTIONAL_SERVICE2)
                        || data.getProvidedServices().contains(OPTIONAL_SERVICE3))) { // 기본 조건 만족
            List<String> serviceList = servicesRepository.findProvidedServicesByDesigner(designer);
            for (String newServiceCode : data.getProvidedServices()) {
                if (!serviceList.contains(newServiceCode)
                        && !newServiceCode.startsWith(REQUIRED_SERVICE)) {
                    Services newService =
                            Services.builder()
                                    .designer(designer)
                                    .providedServicesCode(newServiceCode)
                                    .build();
                    servicesRepository.save(newService); // 등록
                }
            }
            for (String preServiceCode : serviceList) {
                if (!data.getProvidedServices().contains(preServiceCode)
                        && !preServiceCode.startsWith(REQUIRED_SERVICE)) {
                    List<Services> deleteServiceList =
                            servicesRepository.findByProvidedServicesCodeAndDesigner(
                                    preServiceCode, designer);
                    if (deleteServiceList != null && !deleteServiceList.isEmpty()) {
                        for (Services deleteService : deleteServiceList) {
                            priceRepository.deleteByServiceCodeAndDesigner(
                                    deleteService.getProvidedServicesCode(),
                                    designer.getDesignerId());
                        }
                    }
                    servicesRepository.deleteByDesignerAndProvidedServicesCode(
                            designer, preServiceCode); // 삭제
                }
            }
        } else {
            throw new ApiException(ErrorCode.REQUIRED_DATA_NOT_PROVIDED);
        }
        // 미용 가능 견종 코드 목록 (대분류)
        if (data.getPossibleBreed() != null) {
            List<String> breedList =
                    breedsRepository.findPossibleMajorBreedCodesByDesigner(designer);
            for (String newBreedCode : data.getPossibleBreed()) {
                if (!breedList.contains(newBreedCode)) {
                    Breeds newBreeds =
                            Breeds.builder()
                                    .designer(designer)
                                    .possibleMajorBreedCode(newBreedCode)
                                    .build();
                    breedsRepository.save(newBreeds); // 등록
                }
            }
            for (String preBreedCode : breedList) {
                if (!data.getPossibleBreed().contains(preBreedCode)) {
                    breedsRepository.deleteByDesignerAndPossibleMajorBreedCode(
                            designer, preBreedCode); // 삭제
                }
            }
        }
        // 자격증 이미지 목록
        List<String> certList = certificateRepository.findImgUrlsByDesigner(designer);
        for (String preCertImgUrl : certList) { // 기존 인증서 url (a b c)
            if (!data.getPreCertifications().contains(preCertImgUrl)) { // 남은 인증서 url (a b)
                certificateRepository.deleteByDesignerAndImgUrl(designer, preCertImgUrl); // 삭제
            }
        }
        if (data.getNewCertifications() != null) {
            for (String newCertImgUrl : data.getNewCertifications()) { // 신규 인증서 url (d)
                Certificate certificate =
                        Certificate.builder().designer(designer).imgUrl(newCertImgUrl).build();
                certificateRepository.save(certificate); // 등록
            }
        }
        // 디자이너 정보
        Coordinate coordinate = externalService.addressToCoordinate(data.getAddress2());
        Designer updateDesigner =
                designer.toBuilder()
                        .officialName(data.getNickname())
                        .designerImgUrl(imageUrl)
                        .address1(data.getAddress1())
                        .address2(data.getAddress2())
                        .detailAddress(data.getDetailAddress())
                        .xPosition(coordinate.getX())
                        .yPosition(coordinate.getY())
                        .introduction(data.getIntroduction())
                        .phone(data.getPhone())
                        .businessNumber(data.getBusinessNumber())
                        .businessIsVerified(data.getBusinessIsVerified())
                        .updatedAt(LocalDateTime.now())
                        .build();
        designerRepository.save(updateDesigner);
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
        // 신규
        if (data.getPortfolioId() == null || data.getPortfolioId() == 0) {
            String videoUrl = "";
            if (data.getNewVideoUrl() != null) {
                videoUrl = data.getNewVideoUrl();
            }
            Portfolio newPortfolio =
                    Portfolio.builder()
                            .designer(designer)
                            .videoUrl(videoUrl)
                            .title(data.getTitle())
                            .contents(data.getContents())
                            .createdAt(LocalDateTime.now())
                            .build();
            Portfolio savedPortfolio = portfolioRepository.save(newPortfolio); // 등록

            // 신규 이미지 등록
            for (String imgUrl : data.getNewImgUrlList()) {
                PortfolioImg newPortfolioImg =
                        PortfolioImg.builder()
                                .portfolio(savedPortfolio)
                                .imgUrl(imgUrl)
                                .createdAt(LocalDateTime.now())
                                .build();
                imgRepository.save(newPortfolioImg); // 등록
            }

        } else { // 수정
            Portfolio portfolio =
                    portfolioRepository.findByDesignerDesignerIdAndPortfolioId(
                            data.getDesignerId(), data.getPortfolioId());
            if (portfolio == null) {
                throw new ApiException(ErrorCode.DATA_NOT_EXIST);
            }

            String videoUrl = portfolio.getVideoUrl();
            if (data.getNewVideoUrl() != null && !data.getNewVideoUrl().isEmpty()) { // 신규 영상 업데이트
                videoUrl = data.getNewVideoUrl();
            }

            List<PortfolioImg> preImgUrlList = imgRepository.findByPortfolio(portfolio);

            if (data.getPreImgUrlList().size() < preImgUrlList.size()) {
                for (PortfolioImg preImgUrl : preImgUrlList) { // 기존 사진 url (a b c)
                    if (!data.getPreImgUrlList()
                            .contains(preImgUrl.getImgUrl())) { // 남은 사진 url (a b)
                        imgRepository.deleteByPortfolioAndImgUrl(
                                portfolio, preImgUrl.getImgUrl()); // 삭제
                    }
                }
            }
            // 사진 등록
            if (data.getNewImgUrlList() != null && !data.getNewImgUrlList().isEmpty()) {
                for (String newImgUrl : data.getNewImgUrlList()) { // 신규 사진 url (d)
                    PortfolioImg newPortfolioImg =
                            PortfolioImg.builder().portfolio(portfolio).imgUrl(newImgUrl).build();
                    imgRepository.save(newPortfolioImg); // 신규 이미지 등록
                }
            }
            Portfolio updatePortfolio =
                    portfolio.toBuilder()
                            .designer(designer)
                            .videoUrl(videoUrl)
                            .title(data.getTitle())
                            .contents(data.getContents())
                            .updatedAt(LocalDateTime.now())
                            .build();
            portfolioRepository.save(updatePortfolio); // 수정
        }
    } // updateDesignerPortfolio

    /**
     * @title 디자이너 - 포트폴리오 삭제
     * @description 선택한 포트폴리오 삭제
     * @param designerId 디자이너 아이디
     * @param portfolioId 포트폴리오 아이디
     */
    public void deleteDesignerPortfolio(Long designerId, Long portfolioId) {
        // 포트폴리오 조회
        Portfolio portfolio =
                portfolioRepository.findByDesignerDesignerIdAndPortfolioId(designerId, portfolioId);
        if (portfolio == null) {
            throw new ApiException(ErrorCode.DATA_NOT_EXIST);
        } else {
            imgRepository.deleteAll(portfolio.getPortfolioImgs());
            portfolioRepository.delete(portfolio);
        }
    } // deleteDesignerPortfolio

    /**
     * @title 디자이너 - 회원가입
     * @param designerSignupInfo 입력 내용
     * @param kakaoDTO 카카오 이메일, 가입 경로 포함 정보
     * @description 디자이너 회원가입 처리 후 생성된 아이디를 반환한다.
     * @return Long 생성된 디자이너 아이디
     */
    @Transactional
    public Map<String, Long> insertDesigner(DesignerSignup designerSignupInfo, KakaoDTO kakaoDTO) {

        String email = kakaoDTO.getEmail();
        String loginId = email + "_" + kakaoDTO.getId();
        String role = kakaoDTO.getRole();
        if ("D".equals(role)) role = "designer";

        Map<String, Long> result = new HashMap<>();
        Long designerId = 0L;
        if (designerSignupInfo != null) {
            // 중복 email 확인
            Optional<Designer> isExistDesigner = designerRepository.findByEmail(email);
            if (!isExistDesigner.isPresent()) {
                Designer newDesigner =
                        Designer.builder()
                                .role(role)
                                .password("kakaoLoginDesignerPassword")
                                .designerLoginId(loginId)
                                .email(email)
                                .designerName(designerSignupInfo.getDesignerName())
                                .birthDate(
                                        ValidationUtil.stringToDate(
                                                designerSignupInfo.getBirthDate()))
                                .gender(designerSignupInfo.getGender())
                                .phone(designerSignupInfo.getPhone())
                                .officialName(designerSignupInfo.getNickname())
                                .createdAt(LocalDateTime.now())
                                .updatedAt(null) // 신규 가입 시에는 null
                                .build();
                Designer savedDesigner = designerRepository.save(newDesigner);
                designerId = savedDesigner.getDesignerId(); // 생성된 designerId 반환
                result.put("designerId", designerId);
            } else {
                throw new ApiException(ErrorCode.DATA_ALREADY_EXISTS);
            }
        } else {
            // 입력된 값이 null 예외
            throw new ApiException(ErrorCode.ACCOUNT_DATA_ERROR);
        }
        return result;
    } // insertDesigner

    /**
     * @title 디자이너 - 프로필 등록
     * @description 디자이너 회원가입 프로필 등록, 가능견종, 서비스, 인증서 등록
     * @param data 입력 정보
     */
    @Transactional
    public void registerDesignerProfile(DesignerRegister data) {
        // 디자이너 회원가입 정보 조회
        Designer designer =
                designerRepository
                        .findById(data.getDesignerId())
                        .orElseThrow(() -> new ApiException(ErrorCode.DESIGNER_NOT_EXIST));

        Coordinate coordinate = externalService.addressToCoordinate(data.getAddress2());
        Designer updatedDesigner =
                designer.toBuilder()
                        .officialName(
                                data.getNickname() != null
                                        ? data.getNickname()
                                        : designer.getOfficialName())
                        .designerImgUrl(
                                data.getNewImgUrl() != null
                                        ? data.getNewImgUrl()
                                        : designer.getDesignerImgUrl())
                        .address1(
                                data.getAddress1() != null
                                        ? data.getAddress1()
                                        : designer.getAddress1())
                        .address2(
                                data.getAddress2() != null
                                        ? data.getAddress2()
                                        : designer.getAddress2())
                        .detailAddress(
                                data.getDetailAddress() != null
                                        ? data.getDetailAddress()
                                        : designer.getDetailAddress())
                        .xPosition(coordinate.getX())
                        .yPosition(coordinate.getY())
                        .introduction(
                                data.getIntroduction() != null
                                        ? data.getIntroduction()
                                        : designer.getIntroduction())
                        .phone(data.getPhone() != null ? data.getPhone() : designer.getPhone())
                        .businessNumber(
                                data.getBusinessNumber() != null
                                        ? data.getBusinessNumber()
                                        : designer.getBusinessNumber())
                        .businessIsVerified(
                                data.getBusinessIsVerified() != null
                                        ? data.getBusinessIsVerified()
                                        : designer.getBusinessIsVerified())
                        .workExperience(
                                data.getWorkExperience() != null
                                        ? data.getWorkExperience()
                                        : designer.getWorkExperience())
                        .dayOff(
                                data.getDayOff() != null
                                        ? String.join(",", data.getDayOff())
                                        : designer.getDayOff()) // ["월", "화"] -> 월,화
                        .build(); // 빌더로 새로운 객체 생성
        designerRepository.save(updatedDesigner);

        // 서비스별 견종 가격 및 시간 목록
        for (ProvidedServices providedServices : data.getProvidedServiceList()) {
            Services newService =
                    Services.builder()
                            .designer(designer)
                            .providedServicesCode(providedServices.getServiceCode()) // 제공 서비스 코드
                            .build();
            Services savedService = servicesRepository.save(newService);

            for (BreedPriceTime breedPriceTime : providedServices.getBreedPriceTimeList()) {
                Breeds existingBreed =
                        breedsRepository.findByDesignerAndPossibleMajorBreedCode(
                                designer, breedPriceTime.getMajorBreedCode());
                // 없으면 새로운 Breeds 객체 생성
                Breeds savedBreed;
                if (existingBreed == null) {
                    savedBreed =
                            Breeds.builder()
                                    .designer(designer)
                                    .possibleMajorBreedCode(
                                            breedPriceTime.getMajorBreedCode()) // 견종 대분류 코드
                                    .build();
                    savedBreed = breedsRepository.save(savedBreed);
                } else {
                    savedBreed = existingBreed;
                }

                try {
                    Price price =
                            Price.builder()
                                    .service(savedService)
                                    .breed(savedBreed)
                                    .price(
                                            new BigDecimal(
                                                    breedPriceTime
                                                            .getPrice())) // 가격을 BigDecimal로 변환
                                    .time(
                                            Integer.parseInt(
                                                    breedPriceTime.getTime())) // 시간을 Integer로 변환
                                    .build();
                    priceRepository.save(price);

                } catch (NumberFormatException e) {
                    throw new ApiException(ErrorCode.ACCOUNT_DATA_ERROR);
                }
            }
        }

        // 신규 자격증 이미지 URL 목록
        for (String imgUrl : data.getCertificationsUrlList()) {
            Certificate certificate =
                    Certificate.builder().designer(designer).imgUrl(imgUrl).build();
            certificateRepository.save(certificate);
        }

        // 포트폴리오 목록 처리
        for (PortfolioInfoUrl portfolioInfo : data.getPortfolioList()) {
            Portfolio portfolio =
                    Portfolio.builder()
                            .designer(designer)
                            .videoUrl(portfolioInfo.getNewVideoUrl())
                            .videoName(
                                    portfolioInfo.getNewVideoUrl() != null
                                            ? "Video " + portfolioInfo.getNewVideoUrl().hashCode()
                                            : "")
                            .title(portfolioInfo.getTitle())
                            .contents(portfolioInfo.getContents())
                            .createdAt(LocalDateTime.now())
                            .updatedAt(null) // 신규 가입 시에는 null
                            .build();
            Portfolio savedPortfolio = portfolioRepository.save(portfolio);

            // 이미지 URL 목록 처리
            if (portfolioInfo.getNewImgUrlList() != null
                    && !portfolioInfo.getNewImgUrlList().isEmpty()) {
                for (String imgUrl : portfolioInfo.getNewImgUrlList()) {
                    PortfolioImg portfolioImg =
                            PortfolioImg.builder()
                                    .portfolio(savedPortfolio)
                                    .imgUrl(imgUrl)
                                    .createdAt(LocalDateTime.now())
                                    .updatedAt(null) // 신규 가입 시에는 null
                                    .build();
                    imgRepository.save(portfolioImg);
                }
            }
        }
    } // registerDesignerProfile
}
