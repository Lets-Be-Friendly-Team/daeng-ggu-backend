package com.ureca.request.presentation;

import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.request.application.RequestService;
import com.ureca.request.presentation.dto.RequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daenggu")
public class RequestController {

    private final RequestService requestService;

    @PutMapping("/bid/request")
    ResponseDto<String> makeRequest(RequestDto.Request request){
        requestService.makeRequest(request);
        return ResponseUtil.SUCCESS("견적 요청서 생성이 완료되었습니다.", null);
    }

    @PostMapping("/bid/request")
    ResponseDto<String> selectRequest(RequestDto.Request request){
        requestService.selectRequest(request.getRequest_id());
        return ResponseUtil.SUCCESS("견적 요청서 생성이 완료되었습니다.", null);
    }


}
