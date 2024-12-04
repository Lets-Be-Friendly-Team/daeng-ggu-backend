package com.ureca.request.presentation;

import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.request.application.RequestService;
import com.ureca.request.presentation.dto.RequestDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daenggu")
public class RequestController {

    private final RequestService requestService;

    @PostMapping("/bid/request/pet")
    ResponseDto<List<RequestDto.Response>> selectPetProfile(
            @RequestBody RequestDto.Request request) {
        List<RequestDto.Response> responses =
                requestService.selectPetProfile(request.getCustomerId());
        return ResponseUtil.SUCCESS("반려견 조회가 완료되었습니다.", responses);
    }

    @PostMapping("/bid/request/profile")
    ResponseDto<RequestDto.Response> selectPetProfileDetail(
            @RequestBody RequestDto.Request request) {
        RequestDto.Response response =
                requestService.selectPetProfileDetail(request.getCustomerId(), request.getPetId());
        return ResponseUtil.SUCCESS("반려견 프로필 조회가 완료되었습니다.", response);
    }

    @PutMapping("/bid/request")
    ResponseDto<String> makeRequest(@RequestBody RequestDto.Request request) {
        requestService.makeRequest(request);
        return ResponseUtil.SUCCESS("견적 요청서 생성이 완료되었습니다.", null);
    }
}
