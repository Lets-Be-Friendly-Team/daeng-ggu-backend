package com.ureca.profile.presentation;

import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.profile.application.service.PetService;
import com.ureca.profile.presentation.dto.PetDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/daengggu")
@RestController
public class PetController {

    private static final Logger logger = LoggerFactory.getLogger(PetController.class);

    @Autowired private PetService petService;

    /**
     * @title 반려견 - 프로필 상세 조회
     * @param customerId 보호자 아이디
     * @param petId 반려견 아이디
     * @description /daengggu/pet/profile/detail
     */
    @GetMapping("/pet/profile/detail")
    public ResponseDto<PetDetail> petDetail(
            @RequestParam(defaultValue = "") Long customerId,
            @RequestParam(defaultValue = "") Long petId) {
        // service - 반려견 프로필 상세 조회
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", petService.getPetDetail(customerId, petId));
    } // petDetail
}
