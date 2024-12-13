package com.ureca.profile.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.profile.application.CustomerService;
import com.ureca.profile.presentation.dto.BookmarkYn;
import com.ureca.profile.presentation.dto.CustomerDetail;
import com.ureca.profile.presentation.dto.CustomerProfile;
import com.ureca.profile.presentation.dto.CustomerUpdate;
import com.ureca.profile.presentation.dto.CustomerViewDesignerProfile;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/daengggu")
@RestController
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired private CustomerService customerService;

    @GetMapping("/customer/profile")
    @Operation(summary = "보호자 프로필", description = "[MYP1000] 보호자 프로필 조회 API")
    public ResponseDto<CustomerProfile> customerProfile(
            @RequestParam(defaultValue = "") Long customerId) {
        // service - 보호자 프로필
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", customerService.getCustomerProfile(customerId));
    }

    @GetMapping("/customer/profile/detail")
    @Operation(summary = "보호자 프로필 상세 조회", description = "[MYP2000] 보호자 프로필 상세 조회 API")
    public ResponseDto<CustomerDetail> customerDetail(
            @RequestParam(defaultValue = "") Long customerId) {
        // service - 보호자 프로필 상세 조회
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", customerService.getCustomerDetail(customerId));
    }

    @PostMapping(
            value = "/customer/profile/update",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "보호자 프로필 수정", description = "[MYP2000] 보호자 프로필 수정 API")
    public ResponseDto<Void> customerUpdate(
            @RequestPart @Valid CustomerUpdate data,
            @RequestPart(value = "newCustomerImgFile", required = false)
                    MultipartFile newCustomerImgFile)
            throws IOException {
        // service - 보호자 프로필 수정
        logger.info("data>>>" + data);
        logger.info("newCustomerImgFile>>>" + newCustomerImgFile);
        customerService.updateCustomerProfile(data, newCustomerImgFile);
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", null);
    }

    @PostMapping(
            value = "/customer/img/test9",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "test9", description = "Post 객체와 파일을 함께 받음")
    public ResponseDto<String> customerUpdateTest9(
            @RequestBody @Valid CustomerUpdate customerUpdate, // DTO 객체 (JSON으로 받음)
            @RequestPart("newCustomerImgFile") MultipartFile newCustomerImgFile // 파일
            ) {
        logger.info("Customer Update: " + customerUpdate);
        logger.info("New Customer Image File: " + newCustomerImgFile);

        // 파일 처리 및 DTO 처리 로직
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", null);
    }

    @GetMapping("/customer/bookmark")
    @Operation(summary = "보호자 디자이너 찜하기", description = "[DMYP1000] 보호자 디자이너 찜하기 API")
    public ResponseDto<BookmarkYn> customerBookmark(
            @RequestParam(defaultValue = "") Long customerId,
            @RequestParam(defaultValue = "") Long designerId,
            @RequestParam(defaultValue = "") Boolean bookmarkYn) {
        // service - 북마크 테이블 업데이트
        return ResponseUtil.SUCCESS(
                "처리가 완료되었습니다.", customerService.updateBookmark(customerId, designerId, bookmarkYn));
    }

    @GetMapping("/customer/designer/profile")
    @Operation(summary = "보호자가 보는 디자이너 프로필", description = "[DMYP1000] 보호자가 보는 디자이너 프로필 API")
    public ResponseDto<CustomerViewDesignerProfile> designerProfile(
            @RequestParam(defaultValue = "") Long customerId,
            @RequestParam(defaultValue = "") Long designerId) {
        // service - 디자이너 프로필
        return ResponseUtil.SUCCESS(
                "처리가 완료되었습니다.",
                customerService.getCustomerViewDesignerProfile(customerId, designerId));
    }

    // TODO IMG TEST
    @PostMapping(
            value = "/customer/img/test1",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Post 파일만", description = "Post 파일만")
    public ResponseDto<Void> customerUpdateTest1(
            @RequestPart(value = "newCustomerImgFile", required = false)
                    MultipartFile newCustomerImgFile) {
        // service - 보호자 프로필 수정
        logger.info("Test 1 ) newCustomerImgFile>>>" + newCustomerImgFile);
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", null);
    }

    @GetMapping(
            value = "/customer/img/test2",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get 파일만", description = "Get 파일만")
    public ResponseDto<Void> customerUpdateTest2(
            @RequestPart(value = "newCustomerImgFile", required = false)
                    MultipartFile newCustomerImgFile) {
        // service - 보호자 프로필 수정
        logger.info("Test 2 ) newCustomerImgFile>>>" + newCustomerImgFile);
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", null);
    }

    @PostMapping(
            value = "/customer/img/test3",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Post 단순 파일만", description = "Post 단순 파일만")
    public ResponseDto<Void> customerUpdateTest3(MultipartFile newCustomerImgFile) {
        // service - 보호자 프로필 수정
        logger.info("Test 3 ) newCustomerImgFile>>>" + newCustomerImgFile);
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", null);
    }

    @PostMapping(
            value = "/customer/img/test4",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Post 객체, 단순 파일", description = "Post 객체, 단순 파일")
    public ResponseDto<Void> customerUpdateTest4(
            @RequestPart @Valid CustomerUpdate data, MultipartFile newCustomerImgFile) {
        // service - 보호자 프로필 수정
        logger.info("Test 4 ) newCustomerImgFile>>>" + newCustomerImgFile);
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", null);
    }

    @PostMapping(
            value = "/customer/img/test5",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Post 객체 String으로, 파일", description = "Post 객체 String으로, 파일")
    public ResponseDto<String> customerUpdateTest5(
            @RequestPart("data") String customerUpdateJson, // JSON 데이터를 String으로 받음
            @RequestPart("newCustomerImgFile") MultipartFile newCustomerImgFile)
            throws JsonProcessingException {

        // reviewRequestJson을 객체로 변환
        CustomerUpdate customerUpdate =
                new ObjectMapper().readValue(customerUpdateJson, CustomerUpdate.class);
        logger.info("Test 5 ) customerUpdate>>>" + customerUpdate);
        logger.info("Test 5 ) newCustomerImgFile>>>" + newCustomerImgFile);
        return ResponseUtil.SUCCESS("완료되었습니다.", null);
    }

    @PostMapping(value = "/customer/img/test6", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Post 객체 String으로, 파일", description = "Post 객체 String으로, 파일")
    public ResponseDto<String> customerUpdateTest6(
            @RequestPart("data") String customerUpdateJson, // JSON 데이터를 String으로 받음
            @RequestPart("newCustomerImgFile") MultipartFile newCustomerImgFile)
            throws JsonProcessingException {

        // reviewRequestJson을 객체로 변환
        CustomerUpdate customerUpdate =
                new ObjectMapper().readValue(customerUpdateJson, CustomerUpdate.class);
        logger.info("Test 6 ) customerUpdate>>>" + customerUpdate);
        logger.info("Test 6 ) newCustomerImgFile>>>" + newCustomerImgFile);
        return ResponseUtil.SUCCESS("완료되었습니다.", null);
    }

    @GetMapping("/customer/img/test7")
    @Operation(summary = "Get 객체 String으로, 파일", description = "Get 객체 String으로, 파일")
    public ResponseDto<String> customerUpdateTest7(
            @RequestPart("data") String customerUpdateJson, // JSON 데이터를 String으로 받음
            @RequestPart("newCustomerImgFile") MultipartFile newCustomerImgFile)
            throws JsonProcessingException {

        // reviewRequestJson을 객체로 변환
        CustomerUpdate customerUpdate =
                new ObjectMapper().readValue(customerUpdateJson, CustomerUpdate.class);
        logger.info("Test 7 ) customerUpdate>>>" + customerUpdate);
        logger.info("Test 7 ) newCustomerImgFile>>>" + newCustomerImgFile);
        return ResponseUtil.SUCCESS("완료되었습니다.", null);
    }

    @PostMapping(
            value = "/customer/img/test8",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "test8", description = "Post 객체와 파일을 함께 받음")
    public ResponseDto<String> customerUpdateTest8(
            @RequestPart("data") @Valid CustomerUpdate customerUpdate, // DTO 객체
            @RequestPart("newCustomerImgFile") MultipartFile newCustomerImgFile // 파일
            ) {
        logger.info("Customer Update: " + customerUpdate);
        logger.info("New Customer Image File: " + newCustomerImgFile);

        // 파일 처리 및 DTO 처리 로직
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", null);
    }
}
