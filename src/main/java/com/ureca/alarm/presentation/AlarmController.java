package com.ureca.alarm.presentation;

import com.ureca.alarm.application.AlarmService;
import com.ureca.alarm.presentation.dto.AlarmDto;
import com.ureca.common.application.AuthService;
import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.review.domain.Enum.AuthorType;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daengggu")
public class AlarmController {

    private final AlarmService alarmService;
    private final AuthService authService;

    @GetMapping(value = "/alarm/subscribe")
    @Operation(summary = "알람 포트 연결", description = "[HOM1000] 클라이언트가 알림 서버 연결 요청.")
    public SseEmitter subscribe(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam Long userId,
            @RequestParam String userType) {

        SseEmitter emitter = new SseEmitter();
        String usertype = "C".equalsIgnoreCase(userType) ? "CUSTOMER" : "DESIGNER";
        alarmService
                .getEmitterMap()
                .put(String.valueOf(usertype).toUpperCase() + String.valueOf(userId), emitter);

        // 클라이언트 연결 해제 시 맵에서 제거
        emitter.onCompletion(
                () ->
                        alarmService
                                .getEmitterMap()
                                .remove(
                                        String.valueOf(usertype).toUpperCase()
                                                + String.valueOf(userId)));
        emitter.onTimeout(
                () ->
                        alarmService
                                .getEmitterMap()
                                .remove(
                                        String.valueOf(usertype).toUpperCase()
                                                + String.valueOf(userId)));

        // 연결 시 미수신 알람을 가져와 클라이언트로 전송
        List<AlarmDto.Response> unreadAlarms =
                alarmService.getUnreadAlarms(userId, AuthorType.valueOf(usertype.toUpperCase()));
        for (AlarmDto.Response alarm : unreadAlarms) {
            try {
                emitter.send(SseEmitter.event().name("alarm").data(alarm)); // 알림 전송
            } catch (IOException e) {
                e.printStackTrace(); // 전송 실패 시 로그
            }
        }

        // 알림 상태를 읽음으로 업데이트
        alarmService.markAlarmsAsRead(userId, AuthorType.valueOf(usertype.toUpperCase()));

        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return emitter; // ResponseDto 대신 SseEmitter를 직접 반환
    }

    //    @PostMapping("/alarm/read")
    //    @Operation(summary = "알람 읽음 처리", description = "[HOM1000] 클라이언트가 특정 알람 읽음 처리.")
    //    public ResponseDto<String> markNotificationAsRead(@RequestBody AlarmDto.Request request) {
    //        alarmService.getUnreadToRead(request.getAlarmId());
    //        return ResponseUtil.SUCCESS("알림 읽음 상태로 업데이트 완료", null);
    //    }

    @GetMapping("/alarm") // TODO : 토큰 수정
    @Operation(summary = "알람 전체 조회", description = "[HOM1000] 알람 내역을 10개씩 끊어서 보여줌.")
    public ResponseDto<List<AlarmDto.Response>> getAlarms(
            HttpServletRequest request, HttpServletResponse response) {
        Long id = authService.getRequestToUserId(request);
        String role = authService.getRequestToRole(request);
        AuthorType authorType =
                "C".equalsIgnoreCase(role) ? AuthorType.CUSTOMER : AuthorType.DESIGNER;
        List<AlarmDto.Response> alarmList = alarmService.getAlarmsByReceiver(id, authorType);
        for (AlarmDto.Response alarm : alarmList) {
            Long alarms = alarm.getAlarmId();
            alarmService.getUnreadToRead(alarms);
        }
        response.setHeader("Set-Cookie", authService.getRequestToCookieHeader(request));
        response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        return ResponseUtil.SUCCESS("알람 조회가 완료되었습니다.", alarmList);
    }
}
