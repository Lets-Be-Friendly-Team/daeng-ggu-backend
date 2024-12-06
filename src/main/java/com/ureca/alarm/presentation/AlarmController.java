package com.ureca.alarm.presentation;

import com.ureca.alarm.application.AlarmService;
import com.ureca.alarm.presentation.dto.AlarmDto;
import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daenggu")
public class AlarmController {

    private final AlarmService alarmService;

    @PostMapping("/alarm/subscribe")
    public ResponseDto<SseEmitter> subscribe(@RequestBody AlarmDto.Request request) {
        SseEmitter emitter = new SseEmitter();
        alarmService
                .getEmitterMap()
                .put(
                        request.getReceiverType().name() + String.valueOf(request.getReceiverId()),
                        emitter);

        // 클라이언트 연결 해제 시 맵에서 제거
        emitter.onCompletion(
                () ->
                        alarmService
                                .getEmitterMap()
                                .remove(
                                        request.getReceiverType().name()
                                                + String.valueOf(request.getReceiverId())));
        emitter.onTimeout(
                () ->
                        alarmService
                                .getEmitterMap()
                                .remove(
                                        request.getReceiverType().name()
                                                + String.valueOf(request.getReceiverId())));

        // 연결 시, 읽지 않은 알림을 보냄
        List<AlarmDto.Response> alarms =
                alarmService.getAlarmsByReceiver(
                        request.getReceiverId(), request.getReceiverType(), request.getPage());

        return ResponseUtil.SUCCESS("알림 연결을 완료하였습니다", emitter);
    }

    @PostMapping("/alarm/read")
    public ResponseDto<String> markNotificationAsRead(@RequestBody AlarmDto.Request request) {
        alarmService.getUnreadToRead(request.getAlarmId());
        return ResponseUtil.SUCCESS("알림 읽음 상태로 업데이트 완료", null);
    }

    @PostMapping("/alarm")
    public ResponseDto<List<AlarmDto.Response>> getAlarms(@RequestBody AlarmDto.Request request) {
        List<AlarmDto.Response> alarmList =
                alarmService.getAlarmsByReceiver(
                        request.getReceiverId(), request.getReceiverType(), request.getPage());
        return ResponseUtil.SUCCESS("알람 조회가 완료되었습니다.", alarmList);
    }
}
