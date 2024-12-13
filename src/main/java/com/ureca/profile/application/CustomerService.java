package com.ureca.profile.application;

import com.ureca.common.application.S3Service;
import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.common.util.ValidationUtil;
import com.ureca.profile.domain.Bookmark;
import com.ureca.profile.domain.Customer;
import com.ureca.profile.domain.Designer;
import com.ureca.profile.domain.Pet;
import com.ureca.profile.infrastructure.BookmarkRepository;
import com.ureca.profile.infrastructure.CustomerRepository;
import com.ureca.profile.infrastructure.DesignerRepository;
import com.ureca.profile.infrastructure.PetRepository;
import com.ureca.profile.presentation.dto.BookmarkInfo;
import com.ureca.profile.presentation.dto.BookmarkYn;
import com.ureca.profile.presentation.dto.BreedSub;
import com.ureca.profile.presentation.dto.CustomerDetail;
import com.ureca.profile.presentation.dto.CustomerProfile;
import com.ureca.profile.presentation.dto.CustomerSignup;
import com.ureca.profile.presentation.dto.CustomerUpdate;
import com.ureca.profile.presentation.dto.PetInfo;
import com.ureca.profile.presentation.dto.ReviewInfo;
import com.ureca.review.domain.Review;
import com.ureca.review.domain.ReviewImage;
import com.ureca.review.infrastructure.ReviewRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired private CustomerRepository customerRepository;
    @Autowired private DesignerRepository designerRepository;
    @Autowired private PetRepository petRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private BookmarkRepository bookmarkRepository;
    @Autowired private S3Service s3Service;

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
                                            reviewInfo.setReviewImgUrl(
                                                    reviewImages.get(0).getReviewImageUrl());
                                    } else { // 이미지가 없는 경우 기본값 설정 (null 처리)
                                        reviewInfo.setReviewImgUrl(null);
                                    }
                                    return reviewInfo;
                                })
                        .collect(Collectors.toList());
        customerProfile.setReviewList(reviewList);

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
     * @title 보호자 - 프로필 등록/수정
     * @description 보호자 프로필 등록/수정
     * @param data 입력 정보
     * @return status 업데이트 성공 여부
     */
    @Transactional
    public void updateCustomerProfile(CustomerUpdate data, MultipartFile newCustomerImgFile) {

        // 신규 등록
        if (data.getCustomerId() == null || data.getCustomerId() == 0) {

            String imageUrl = "", fileName = "";
            // 새로운 이미지 등록
            if (newCustomerImgFile != null && !newCustomerImgFile.getOriginalFilename().isEmpty()) {
                imageUrl =
                        s3Service.uploadFileImage(
                                newCustomerImgFile, "profile", "profile"); // TODO 파일명 짓는 양식 정하기
                fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
            }
            logger.info(">>>>>> Service Start !!! ");
            // 입력 내용
            Customer newCustomer =
                    Customer.builder()
                            .customerName(data.getCustomerName())
                            .customerImgUrl(imageUrl)
                            .customerImgName(fileName)
                            .birthDate(ValidationUtil.stringToDate(data.getBirthDate()))
                            .gender(data.getGender())
                            .phone(data.getPhone())
                            .nickname(data.getNickname())
                            .address1(data.getAddress1())
                            .address2(data.getAddress2())
                            .detailAddress(data.getDetailAddress())
                            .createdAt(LocalDateTime.now())
                            .build();

            // 등록
            customerRepository.save(newCustomer);

        } else {

            logger.info(">>>>>> Service START !!! ");

            // 기존 정보 조회
            Customer customer =
                    customerRepository
                            .findById(data.getCustomerId())
                            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));

            String imageUrl = customer.getCustomerImgUrl();
            String fileName = customer.getCustomerImgName();
            // 이미지 수정 - 같은 파일명으로 덮어쓰기
            if (newCustomerImgFile != null && !newCustomerImgFile.getOriginalFilename().isEmpty()) {
                imageUrl =
                        s3Service.updateFileImage(data.getPreCustomerImgUrl(), newCustomerImgFile);
                fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
            }

            // 입력 내용
            Customer updatedCustomer =
                    customer.toBuilder()
                            .customerName(data.getCustomerName())
                            .customerImgUrl(imageUrl)
                            .customerImgName(fileName)
                            .birthDate(ValidationUtil.stringToDate(data.getBirthDate()))
                            .gender(data.getGender())
                            .phone(data.getPhone())
                            .nickname(data.getNickname())
                            .address1(data.getAddress1())
                            .address2(data.getAddress2())
                            .detailAddress(data.getDetailAddress())
                            .updatedAt(LocalDateTime.now())
                            .build();

            logger.info(">>>>>> Service END !!! ");

            // 업데이트
            customerRepository.save(updatedCustomer);
        }
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
     * @param email 카카오 이메일
     * @param role 회원가입 경로
     * @description 보호자 회원가입 처리 후 생성된 아이디를 반환한다.
     * @return Long 생성된 보호자 아이디
     */
    @Transactional
    public Map<String, Long> insertCustomer(
            CustomerSignup customerSignupInfo, String email, String role) {
        // TODO TEST email 생성
        long count = customerRepository.count(); // long 타입
        int cnt = (int) count + 1;
        email = "test" + cnt + "@naver.com";
        String loginId = email + cnt;

        Map<String, Long> result = new HashMap<>();
        Long customerId = 0L;
        if (customerSignupInfo != null) {
            // 중복 email 확인
            Optional<Customer> isExistCustomer = customerRepository.findByEmail(email);
            if (!isExistCustomer.isPresent()) {
                Customer newCustomer =
                        Customer.builder()
                                .role("customer")
                                .password("1234")
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
                                .xPosition(127.05) // TODO 좌표 변환 API 연동
                                .yPosition(37.5029)
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
    } // updateBookmark
}
