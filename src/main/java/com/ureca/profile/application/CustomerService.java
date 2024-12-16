package com.ureca.profile.application;

import com.ureca.common.application.S3Service;
import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.common.util.ValidationUtil;
import com.ureca.login.application.ExternalService;
import com.ureca.login.application.dto.Coordinate;
import com.ureca.login.presentation.dto.KakaoDTO;
import com.ureca.profile.domain.Bookmark;
import com.ureca.profile.domain.Customer;
import com.ureca.profile.domain.Designer;
import com.ureca.profile.domain.Pet;
import com.ureca.profile.domain.Portfolio;
import com.ureca.profile.infrastructure.BookmarkRepository;
import com.ureca.profile.infrastructure.CertificateRepository;
import com.ureca.profile.infrastructure.CustomerRepository;
import com.ureca.profile.infrastructure.DesignerRepository;
import com.ureca.profile.infrastructure.PetRepository;
import com.ureca.profile.infrastructure.PortfolioImgRepository;
import com.ureca.profile.infrastructure.PortfolioRepository;
import com.ureca.profile.presentation.dto.BookmarkInfo;
import com.ureca.profile.presentation.dto.BookmarkYn;
import com.ureca.profile.presentation.dto.BreedSub;
import com.ureca.profile.presentation.dto.CustomerDetail;
import com.ureca.profile.presentation.dto.CustomerProfile;
import com.ureca.profile.presentation.dto.CustomerSignup;
import com.ureca.profile.presentation.dto.CustomerUpdate;
import com.ureca.profile.presentation.dto.CustomerViewDesignerProfile;
import com.ureca.profile.presentation.dto.DesignerProfile;
import com.ureca.profile.presentation.dto.PetInfo;
import com.ureca.profile.presentation.dto.PortfolioInfo;
import com.ureca.review.domain.Review;
import com.ureca.review.infrastructure.ReviewRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired private CustomerRepository customerRepository;
    @Autowired private DesignerRepository designerRepository;
    @Autowired private CertificateRepository certificateRepository;
    @Autowired private PortfolioRepository portfolioRepository;
    @Autowired private PortfolioImgRepository imgRepository;
    @Autowired private PetRepository petRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private BookmarkRepository bookmarkRepository;
    @Autowired private ExternalService externalService;
    @Autowired private ProfileService profileService;
    @Autowired private S3Service s3Service;

    private static final String CUSTOMER = "C";
    private static final String ALL = "A";

    /**
     * @title 보호자 - 프로필
     * @description 보호자정보, 반려견목록, 리뷰목록, 찜한목록 조회
     * @param customerId 보호자 아이디
     * @return CustomerProfile 보호자 프로필 정보
     */
    public CustomerProfile getCustomerProfile(Long customerId) {

        CustomerProfile customerProfile = new CustomerProfile();

        // 보호자 정보
        Customer customer =
                customerRepository
                        .findById(customerId)
                        .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        // 조회
        customerProfile.setCustomerId(customer.getCustomerId());
        customerProfile.setCustomerName(customer.getCustomerName());
        customerProfile.setCustomerImgUrl(customer.getCustomerImgUrl());
        customerProfile.setCustomerImgName(customer.getCustomerImgName());
        customerProfile.setNickname(customer.getNickname());

        // 반려견 목록
        List<Pet> pets = petRepository.findByCustomerCustomerId(customerId);
        List<PetInfo> petInfoList =
                pets.stream()
                        .map(
                                pet -> {
                                    PetInfo petInfo = new PetInfo();
                                    petInfo.setPetId(pet.getPetId());
                                    petInfo.setPetName(pet.getPetName());
                                    petInfo.setPetImgUrl(pet.getPetImgUrl());
                                    return petInfo;
                                })
                        .collect(Collectors.toList());
        customerProfile.setPetList(petInfoList);

        // 리뷰 목록
        List<Review> reviews = reviewRepository.findByCustomerCustomerId(customerId);
        customerProfile.setReviewList(profileService.reviewToReviewInfo(reviews, CUSTOMER));

        // 찜한 디자이너 정보
        List<Bookmark> bookmarks = bookmarkRepository.findByCustomerCustomerId(customerId);
        List<BookmarkInfo> bookmarkList =
                bookmarks.stream()
                        .map(
                                bookmark -> {
                                    BookmarkInfo bookmarkInfo = new BookmarkInfo();
                                    bookmarkInfo.setDesignerId(
                                            bookmark.getDesigner().getDesignerId());
                                    bookmarkInfo.setNickname(
                                            bookmark.getDesigner().getOfficialName());
                                    bookmarkInfo.setDesignerImgUrl(
                                            bookmark.getDesigner().getDesignerImgUrl());
                                    bookmarkInfo.setDesignerAddress(
                                            bookmark.getDesigner().getAddress1());
                                    List<BreedSub> possibleBreeds =
                                            designerRepository.findDesignerSubBreeds(
                                                    bookmark.getDesigner().getDesignerId());
                                    if (possibleBreeds == null) bookmarkInfo.setPossibleBreed(null);
                                    bookmarkInfo.setPossibleBreed(possibleBreeds);
                                    return bookmarkInfo;
                                })
                        .collect(Collectors.toList());
        customerProfile.setBookmarkList(bookmarkList);

        return customerProfile;
    } // getCustomerProfile

    /**
     * @title 보호자 - 프로필 상세
     * @description 보호자 상세 정보 조회
     * @param customerId 보호자 아이디
     * @return CustomerDetail 보호자 프로필 상세 정보
     */
    public CustomerDetail getCustomerDetail(Long customerId) {

        CustomerDetail customerDetail = new CustomerDetail();

        Customer customer =
                customerRepository
                        .findById(customerId)
                        .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        // 조회
        customerDetail.setCustomerId(customer.getCustomerId());
        customerDetail.setCustomerLoginId(customer.getCustomerLoginId());
        customerDetail.setCustomerName(customer.getCustomerName());
        customerDetail.setCustomerImgUrl(customer.getCustomerImgUrl());
        customerDetail.setCustomerImgName(customer.getCustomerImgName());
        customerDetail.setNickname(customer.getNickname());

        customerDetail.setBirthDate(ValidationUtil.dateToString(customer.getBirthDate()));
        customerDetail.setGender(customer.getGender());
        customerDetail.setPhone(customer.getPhone());

        // 주소 처리
        customerDetail.setAddress1(customer.getAddress1());
        customerDetail.setAddress2(customer.getAddress2());
        customerDetail.setDetailAddress(customer.getDetailAddress());

        // 응답 검증
        if (customerDetail == null) {
            throw new ApiException(ErrorCode.DATA_NOT_EXIST);
        } else {
            logger.info(String.valueOf(customerDetail));
        }

        return customerDetail;
    } // getCustomerDetail

    /**
     * @title 보호자 - 프로필 수정
     * @description 보호자 프로필 수정
     * @param data 입력 정보
     * @return status 업데이트 성공 여부
     */
    @Transactional
    public void updateCustomerProfile(CustomerUpdate data) {
        // 기존 정보 조회
        Customer customer =
                customerRepository
                        .findById(data.getCustomerId())
                        .orElseThrow(() -> new ApiException(ErrorCode.CUSTOMER_NOT_EXIST));
        String imageUrl = customer.getCustomerImgUrl();

        if (!data.getNewCustomerImgUrl().isEmpty()) { // 신규 이미지 업데이트
            imageUrl = data.getNewCustomerImgUrl();
        }
        Coordinate coordinate = externalService.addressToCoordinate(data.getAddress2());
        Customer updatedCustomer =
                customer.toBuilder()
                        .customerName(data.getCustomerName())
                        .customerImgUrl(imageUrl)
                        .birthDate(ValidationUtil.stringToDate(data.getBirthDate()))
                        .gender(data.getGender())
                        .phone(data.getPhone())
                        .nickname(data.getNickname())
                        .address1(data.getAddress1())
                        .address2(data.getAddress2())
                        .detailAddress(data.getDetailAddress())
                        .xPosition(coordinate.getX())
                        .yPosition(coordinate.getY())
                        .updatedAt(LocalDateTime.now())
                        .build();
        customerRepository.save(updatedCustomer); // 업데이트
    } // updateCustomerProfile

    /**
     * @title 보호자 - 디자이너 찜하기
     * @param customerId 보호자 아이디
     * @param designerId 디자이너 아이디
     * @param bookmarkYn 찜 유무
     * @description 보호자와 디자이너 북마크 데이터 추가/삭제
     * @return BookmarkYn 찜 상태
     */
    @Transactional
    public BookmarkYn updateBookmark(Long customerId, Long designerId, Boolean bookmarkYn) {
        if (!bookmarkYn) { // 찜하기
            if (!bookmarkRepository.existsByCustomerCustomerIdAndDesignerDesignerId(
                    customerId, designerId)) {
                Customer customer =
                        customerRepository
                                .findById(customerId)
                                .orElseThrow(() -> new ApiException(ErrorCode.CUSTOMER_NOT_EXIST));
                Designer designer =
                        designerRepository
                                .findById(designerId)
                                .orElseThrow(() -> new ApiException(ErrorCode.DESIGNER_NOT_EXIST));
                Bookmark bookmark =
                        Bookmark.builder().customer(customer).designer(designer).build();
                bookmarkRepository.save(bookmark);
            }
        } else { // 찜취소
            if (bookmarkRepository.existsByCustomerCustomerIdAndDesignerDesignerId(
                    customerId, designerId)) {
                bookmarkRepository.deleteByCustomerCustomerIdAndDesignerDesignerId(
                        customerId, designerId);
            }
        }
        return BookmarkYn.builder().bookmarkYn(!bookmarkYn).build();
    } // updateBookmark

    /**
     * @title 보호자 - 회원가입
     * @param customerSignupInfo 입력 내용
     * @param kakaoDTO 토큰 기준 사용자 정보
     * @description 보호자 회원가입 처리 후 생성된 아이디를 반환한다.
     * @return Long 생성된 보호자 아이디
     */
    @Transactional
    public Map<String, Long> insertCustomer(CustomerSignup customerSignupInfo, KakaoDTO kakaoDTO) {

        String email = kakaoDTO.getEmail();
        String loginId = email + "_" + kakaoDTO.getId();
        String role = kakaoDTO.getRole();
        if ("C".equals(role)) role = "customer";

        Map<String, Long> result = new HashMap<>();
        Long customerId = 0L;
        if (customerSignupInfo != null) {
            // 중복 email 확인
            Optional<Customer> isExistCustomer = customerRepository.findByEmail(email);
            if (!isExistCustomer.isPresent()) {
                Coordinate coordinate =
                        externalService.addressToCoordinate(customerSignupInfo.getAddress2());
                Customer newCustomer =
                        Customer.builder()
                                .role(role)
                                .password("kakaoLoginCustomerPassword")
                                .infoAgree("Y")
                                .customerLoginId(loginId)
                                .email(email)
                                .customerName(customerSignupInfo.getCustomerName())
                                .birthDate(
                                        ValidationUtil.stringToDate(
                                                customerSignupInfo.getBirthDate()))
                                .gender(customerSignupInfo.getGender())
                                .phone(customerSignupInfo.getPhone())
                                .nickname(customerSignupInfo.getNickname())
                                .address1(customerSignupInfo.getAddress1())
                                .address2(customerSignupInfo.getAddress2())
                                .detailAddress(customerSignupInfo.getDetailAddress())
                                .xPosition(coordinate.getX())
                                .yPosition(coordinate.getY())
                                .createdAt(LocalDateTime.now())
                                .updatedAt(null) // 신규 가입 시에는 null
                                .build();
                Customer savedCustomer = customerRepository.save(newCustomer);
                customerId = savedCustomer.getCustomerId(); // 생성된 customerId 반환
                result.put("customerId", customerId);
            } else {
                throw new ApiException(ErrorCode.DATA_ALREADY_EXISTS);
            }
        } else {
            // 입력된 값이 null 예외
            throw new ApiException(ErrorCode.ACCOUNT_DATA_ERROR);
        }
        return result;
    } // insertCustomer

    /**
     * @title 보호자가 보는 디자이너 프로필
     * @param customerId 보호자 아이디
     * @param designerId 디자이너 아이디
     * @description 디자이너정보, 제공서비스, 가능견종, 포트폴리오목록, 리뷰목록 조회, 찜 유무
     * @return CustomerViewDesignerProfile 디자이너 프로필 정보
     */
    public CustomerViewDesignerProfile getCustomerViewDesignerProfile(
            Long customerId, Long designerId) {

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
        designerProfile.setReviewList(profileService.reviewToReviewInfo(reviews, ALL));

        // 찜 여부
        Boolean isBookmarked =
                bookmarkRepository.existsByCustomerCustomerIdAndDesignerDesignerId(
                        customerId, designerId);

        CustomerViewDesignerProfile customerDesignerProfile =
                CustomerViewDesignerProfile.builder()
                        .designerId(designerProfile.getDesignerId())
                        .designerName(designerProfile.getDesignerName())
                        .nickname(designerProfile.getNickname())
                        .designerImgUrl(designerProfile.getDesignerImgUrl())
                        .designerImgName(designerProfile.getDesignerImgName())
                        .address1(designerProfile.getAddress1())
                        .address2(designerProfile.getAddress2())
                        .detailAddress(designerProfile.getDetailAddress())
                        .introduction(designerProfile.getIntroduction())
                        .workExperience(designerProfile.getWorkExperience())
                        .isBookmarked(isBookmarked) // 찜 여부를 설정
                        .reviewStarAvg(designerProfile.getReviewStarAvg()) // 별점 평균
                        .reviewLikeCntAll(designerProfile.getReviewLikeCntAll()) // 리뷰 좋아요 수
                        .providedServices(designerProfile.getProvidedServices()) // 제공 서비스
                        .possibleBreeds(designerProfile.getPossibleBreeds()) // 미용 가능 견종
                        .certifications(designerProfile.getCertifications()) // 자격증 이미지 URL
                        .portfolioList(designerProfile.getPortfolioList()) // 포트폴리오 목록
                        .reviewList(designerProfile.getReviewList()) // 리뷰 목록
                        .build();

        return customerDesignerProfile;
    } // getCustomerViewDesignerProfile
}
