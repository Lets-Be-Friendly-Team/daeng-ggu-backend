package com.ureca.alarm.presentation;

import com.ureca.alarm.application.AlarmService;
import com.ureca.alarm.domain.Alarm;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daenggu")
public class AlarmController {

    private final AlarmService alarmService;



    @GetMapping("/alarm/subscribe")
    public SseEmitter subscribe(@RequestParam Long receiverId) {
        SseEmitter emitter = new SseEmitter();
        alarmService.getEmitterMap().put(receiverId, emitter);

        // 클라이언트 연결 해제 시 맵에서 제거
        emitter.onCompletion(() -> alarmService.getEmitterMap().remove(receiverId));
        emitter.onTimeout(() -> alarmService.getEmitterMap().remove(receiverId));

        // 연결 시, 읽지 않은 알림을 보내고 상태를 읽음으로 변경
        List<Alarm> unreadNotifications = alarmService.getUnreadNotifications(receiverId);
        unreadNotifications.forEach(notification -> {
            try {
                emitter.send(notification.getAlarm_message());  // 알림 전송
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // 알림 상태를 읽음으로 업데이트
        alarmService.markNotificationsAsRead(receiverId);

        return emitter;
    }
}
