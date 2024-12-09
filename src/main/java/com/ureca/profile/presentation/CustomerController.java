package com.ureca.profile.presentation;

import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.profile.application.CustomerService;
import com.ureca.profile.presentation.dto.BookmarkYn;
import com.ureca.profile.presentation.dto.CustomerDetail;
import com.ureca.profile.presentation.dto.CustomerProfile;
import com.ureca.profile.presentation.dto.CustomerUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/daengggu")
@RestController
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired private CustomerService customerService;

    /**
     * @title 보호자 - 프로필
     * @param customerId 보호자 아이디
     * @description /daengggu/customer/profile
     */
    @GetMapping("/customer/profile")
    public ResponseDto<CustomerProfile> customerProfile(
            @RequestParam(defaultValue = "") Long customerId) {
        // service - 보호자 프로필
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", customerService.getCustomerProfile(customerId));
    } // customerProfile

    /**
     * @title 보호자 - 프로필 상세 조회
     * @param customerId 보호자 아이디
     * @description /daengggu/customer/profile/detail
     */
    @GetMapping("/customer/profile/detail")
    public ResponseDto<CustomerDetail> customerDetail(
            @RequestParam(defaultValue = "") Long customerId) {
        // service - 보호자 프로필 상세 조회
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", customerService.getCustomerDetail(customerId));
    } // customerDetail

    /**
     * @title 보호자 - 프로필 수정
     * @param data 입력 내용
     * @description /daengggu/customer/profile/update
     */
    @GetMapping("/customer/profile/update")
    public ResponseDto<Void> customerUpdate(@ModelAttribute CustomerUpdate data) {
        // service - 보호자 프로필 수정
        customerService.updateCustomerProfile(data);
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", null);
    } // customerUpdate

    /**
     * @title 보호자 - 디자이너 찜하기
     * @param customerId 보호자 아이디
     * @param designerId 디자이너 아이디
     * @param bookmarkYn 찜 유무
     * @description /daengggu/customer/bookmark
     */
    @GetMapping("/customer/bookmark")
    public ResponseDto<BookmarkYn> customerBookmark(
            @RequestParam(defaultValue = "") Long customerId,
            @RequestParam(defaultValue = "") Long designerId,
            @RequestParam(defaultValue = "") Boolean bookmarkYn) {
        // service - 북마크 테이블 업데이트
        return ResponseUtil.SUCCESS(
                "처리가 완료되었습니다.", customerService.updateBookmark(customerId, designerId, bookmarkYn));
    } // customerBookmark
}
