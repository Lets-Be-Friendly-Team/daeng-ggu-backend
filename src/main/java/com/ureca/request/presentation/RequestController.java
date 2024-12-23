package com.ureca.request.presentation;

import com.ureca.common.application.AuthService;
import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.request.application.RequestService;
import com.ureca.request.presentation.dto.RequestDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daengggu")
public class RequestController {

    private final RequestService requestService;
    private final AuthService authService;

    @GetMapping("/bid/request/pet")
    @Operation(summary = "반려견 프로필 조회", description = "[REQ1000] 요청서 작성 페이지에서 반려견 프로필 조회")
    ResponseDto<List<RequestDto.Response>> selectPetProfile(
            HttpServletRequest request, HttpServletResponse response) {
        // TODO : 토큰 수정
        Long id = authService.getRequestToUserId(request);
        List<RequestDto.Response> responses = requestService.selectPetProfile(id);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS("반려견 조회가 완료되었습니다.", responses);
    }

    /**
     * 견적 요청서 생성 API 새로운 견적 요청서를 생성합니다.
     *
     * @param request 견적 요청 정보를 포함하는 요청 DTO
     * @return 요청서 생성 완료 메시지가 포함된 응답 DTO
     */
    @PutMapping("/bid/request")
    @Operation(summary = "요청서 생성", description = "[REQ1000] 요청서를 생성하고, 분류 후 알람까지 감.")
    ResponseDto<String> makeRequest(
            @RequestBody RequestDto.Request requestDto,
            HttpServletRequest request,
            HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        requestService.makeRequest(requestDto);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS("견적 요청서 생성이 완료되었습니다.", null);
    }

    /**
     * 특정 견적 요청서 조회 API 요청 ID를 기반으로 특정 견적 요청서를 조회합니다.
     *
     * @param requestId 요청 ID를 포함하는 요청 DTO
     * @return 요청서 정보가 포함된 응답 DTO
     */
    @PostMapping("/bid/request")
    @Operation(summary = "요청서 세부조회", description = "[REQ1300] 디자이너,보호자가 요청서 정보 세부 조회 API.")
    ResponseDto<RequestDto.Response> selectRequest(
            @RequestBody RequestDto.ID requestId,
            HttpServletRequest request,
            HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        RequestDto.Response responseDto = requestService.selectRequest(requestId.getRequestId());
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS("견적 요청서 조회가 완료되었습니다.", responseDto);
    }

    @GetMapping("/bid/request/designer")
    @Operation(summary = "디자이너 요청서 조회", description = "[DREQ1300] 디자이너에게서 온 견적요청서 list 조회.")
    ResponseDto<List<RequestDto.Response>> selectDesignerRequest(
            HttpServletRequest request, HttpServletResponse response) {
        // TODO : 토큰 수정
        Long id = authService.getRequestToUserId(request);
        List<RequestDto.Response> reqList = requestService.selectDesignerRequest(id);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS("견적 요청서 리스트 조회가 완료되었습니다.", reqList);
    }

    @GetMapping("/bid/request/customer")
    @Operation(summary = "보호자 견적요청서 조회", description = "[REQ3000] 보호자 지난 견적요청서 list 조회.")
    ResponseDto<List<RequestDto.Response>> selectRequestBefore(
            HttpServletRequest request, HttpServletResponse response) {
        // TODO : 토큰 수정
        Long id = authService.getRequestToUserId(request);
        List<RequestDto.Response> responseDto = requestService.selectRequestBefore(id);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS("견적 요청서 리스트 조회가 완료되었습니다.", responseDto);
    }

    /**
     * 요청서 삭제 API 요청 ID를 기반으로 해당 요청서를 삭제합니다.
     *
     * @param request_id 요청서 ID
     * @return 요청서 삭제 완료 메시지가 포함된 응답 DTO
     */
    @DeleteMapping("/bid/request")
    @Operation(summary = "보호자 견적요청서 삭제", description = "[DREQ1300] 결제 이전 단계에 있는 견적요청서를 삭제할 수 있음.")
    public ResponseDto<Void> deleteRequest(
            @RequestParam Long request_id,
            HttpServletRequest request,
            HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        requestService.deleteRequest(request_id);
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS("요청서 삭제가 완료되었습니다.", null);
    }
}
