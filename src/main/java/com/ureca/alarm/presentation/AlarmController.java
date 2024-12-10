package com.ureca.alarm.presentation;

import com.ureca.alarm.application.AlarmService;
import com.ureca.alarm.presentation.dto.AlarmDto;
import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.review.domain.Enum.AuthorType;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daengggu")
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping("/alarm/subscribe")
    @Operation(summary = "알람 포트 연결", description = "[HOM1000] 클라이언트가 알림 서버 연결 요청.")
    public ResponseDto<SseEmitter> subscribe() { // TODO : 토큰 수정
        SseEmitter emitter = new SseEmitter();
        alarmService.getEmitterMap().put("CUSTOMER" + String.valueOf(1L), emitter);

        // 클라이언트 연결 해제 시 맵에서 제거
        emitter.onCompletion(
                () -> alarmService.getEmitterMap().remove("CUSTOMER" + String.valueOf(1L)));
        emitter.onTimeout(
                () -> alarmService.getEmitterMap().remove("CUSTOMER" + String.valueOf(1L)));

        // 연결 시, 읽지 않은 알림을 보냄
        List<AlarmDto.Response> alarms =
                alarmService.getAlarmsByReceiver(1L, AuthorType.valueOf("CUSTOMER"), 0);

        return ResponseUtil.SUCCESS("알림 연결을 완료하였습니다", emitter);
    }

    @PostMapping("/alarm/read")
    @Operation(summary = "알람 읽음 처리", description = "[HOM1000] 클라이언트가 특정 알람 읽음 처리.")
    public ResponseDto<String> markNotificationAsRead(@RequestBody AlarmDto.Request request) {
        alarmService.getUnreadToRead(request.getAlarmId());
        return ResponseUtil.SUCCESS("알림 읽음 상태로 업데이트 완료", null);
    }

    @PostMapping("/alarm") // TODO : 토큰 수정
    @Operation(summary = "알람 전체 조회", description = "[HOM1000] 알람 내역을 10개씩 끊어서 보여줌.")
    public ResponseDto<List<AlarmDto.Response>> getAlarms(@RequestBody AlarmDto.Request request) {
        List<AlarmDto.Response> alarmList =
                alarmService.getAlarmsByReceiver(
                        2L, AuthorType.valueOf("CUSTOMER"), request.getPage());
        return ResponseUtil.SUCCESS("알람 조회가 완료되었습니다.", alarmList);
    }
}
