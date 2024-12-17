package com.ureca.profile.presentation;

import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.profile.application.PetService;
import com.ureca.profile.presentation.dto.PetDetail;
import com.ureca.profile.presentation.dto.PetUpdate;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/daengggu")
@RestController
public class PetController {

    private static final Logger logger = LoggerFactory.getLogger(PetController.class);

    @Autowired private PetService petService;

    @GetMapping("/pet/profile/detail")
    @Operation(summary = "반려견 프로필 상세", description = "[MYP3000] 반려견 프로필 상세 조회 API")
    public ResponseDto<PetDetail> petDetail(
            @RequestParam(defaultValue = "") Long customerId,
            @RequestParam(defaultValue = "") Long petId) {
        // service - 반려견 프로필 상세 조회
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", petService.getPetDetail(customerId, petId));
    }

    @PatchMapping("/pet/profile/update")
    @Operation(summary = "반려견 프로필 수정", description = "[MYP3000] 반려견 프로필 수정 API")
    public ResponseDto<Void> petUpdate(@RequestBody PetUpdate data) {
        // service - 반려견 프로필 수정
        petService.updatePetProfile(data);
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", null);
    }

    @DeleteMapping("/pet/profile/delete")
    @Operation(summary = "반려견 프로필 삭제", description = "[MYP1000] 반려견 프로필 삭제 API")
    public ResponseDto<Void> petDelete(
            @RequestParam(defaultValue = "") Long customerId,
            @RequestParam(defaultValue = "") Long petId) {
        // service - 반려견 프로필 삭제
        petService.deletePet(customerId, petId);
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", null);
    }
}
