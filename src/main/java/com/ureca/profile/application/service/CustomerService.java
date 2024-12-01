package com.ureca.profile.application.service;

import com.ureca.common.application.S3Service;
import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.common.util.ValidationUtil;
import com.ureca.profile.domain.Bookmark;
import com.ureca.profile.domain.Breeds;
import com.ureca.profile.domain.Customer;
import com.ureca.profile.domain.Pet;
import com.ureca.profile.infrastructure.BookmarkRepository;
import com.ureca.profile.infrastructure.CustomerRepository;
import com.ureca.profile.infrastructure.PetRepository;
import com.ureca.profile.presentation.dto.BookmarkInfo;
import com.ureca.profile.presentation.dto.CustomerDetail;
import com.ureca.profile.presentation.dto.CustomerProfile;
import com.ureca.profile.presentation.dto.CustomerUpdate;
import com.ureca.profile.presentation.dto.PetInfo;
import com.ureca.profile.presentation.dto.ReviewInfo;
import com.ureca.review.domain.Review;
import com.ureca.review.domain.ReviewImage;
import com.ureca.review.infrastructure.ReviewRepository;
import java.text.SimpleDateFormat;
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
                        .orElseThrow(() -> new RuntimeException("Customer not found"));
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
                                    bookmarkInfo.setDesignerImgUrl(
                                            bookmark.getDesigner().getDesignerImgUrl());
                                    bookmarkInfo.setDesignerAddress(
                                            bookmark.getDesigner().getAddress1()
                                                    + bookmark.getDesigner().getAddress2());

                                    List<Breeds> breedsList = bookmark.getDesigner().getBreeds();
                                    if (breedsList != null) {
                                        String[] breedArray =
                                                breedsList.stream()
                                                        .map(
                                                                breed ->
                                                                        breed
                                                                                .toString()) // Breeds 객체에서 String 값을 추출하여 String 배열로 변환
                                                        .toArray(
                                                                String[]::new); // 변환된 배열을 String[]로
                                        // 수집
                                        bookmarkInfo.setPossibleBreed(
                                                breedArray); // 변환된 breedArray를 possibleBreed에 설정
                                    } else {
                                        bookmarkInfo.setPossibleBreed(
                                                new String[0]); // null인 경우 빈 배열로 설정
                                    }
                                    return bookmarkInfo;
                                })
                        .collect(Collectors.toList());
        customerProfile.setBookmarkList(bookmarkList);

        // 응답 검증
        if (customerProfile == null) {
            throw new ApiException(ErrorCode.DATA_NOT_EXIST);
        } else {
            logger.info(String.valueOf(customerProfile));
        }

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
                        .orElseThrow(() -> new RuntimeException("Customer not found"));
        // 조회
        customerDetail.setCustomerId(customer.getCustomerId());
        customerDetail.setCustomerLoginId(customer.getCustomerLoginId());
        customerDetail.setCustomerName(customer.getCustomerName());
        customerDetail.setCustomerImgUrl(customer.getCustomerImgUrl());
        customerDetail.setCustomerImgName(customer.getCustomerImgName());
        customerDetail.setNickname(customer.getNickname());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(customer.getBirthDate());
        customerDetail.setBirthDate(formattedDate);
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
    public void updateCustomerProfile(CustomerUpdate data) {

        // 신규 등록
        if (data.getCustomerId() == null || data.getCustomerId() == 0) {

            String imageUrl = "", fileName = "";
            // 새로운 이미지 등록
            if (data.getNewCustomerImgFile() != null
                    && !data.getNewCustomerImgFile().getOriginalFilename().isEmpty()) {
                imageUrl =
                        s3Service.uploadFileImage(
                                data.getNewCustomerImgFile(),
                                "profile",
                                "profile"); // TODO 파일명 짓는 양식 정하기
                fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
            }

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
            // 기존 정보 조회
            Customer customer =
                    customerRepository
                            .findById(data.getCustomerId())
                            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));

            String imageUrl = customer.getCustomerImgUrl();
            String fileName = customer.getCustomerImgName();
            // 이미지 수정 - 같은 파일명으로 덮어쓰기
            if (data.getNewCustomerImgFile() != null
                    && !data.getNewCustomerImgFile().getOriginalFilename().isEmpty()) {
                imageUrl =
                        s3Service.updateFileImage(
                                data.getPreCustomerImgUrl(), data.getNewCustomerImgFile());
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

            // 업데이트
            customerRepository.save(updatedCustomer);
        }
    } // updateCustomerProfile
}
