package com.ureca.alarm.application;

import com.ureca.alarm.domain.Alarm;
import com.ureca.alarm.infrastructure.AlarmRepository;
import com.ureca.alarm.presentation.dto.AlarmDto;
import com.ureca.estimate.infrastructure.EstimateRepository;
import com.ureca.profile.infrastructure.CustomerRepository;
import com.ureca.profile.infrastructure.DesignerRepository;
import com.ureca.request.infrastructure.RequestRepository;
import com.ureca.reservation.infrastructure.ReservationRepository;
import com.ureca.review.infrastructure.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final RequestRepository requestRepository;
    private final EstimateRepository estimateRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;
    private final DesignerRepository designerRepository;
    private final CustomerRepository customerRepository;

    // 사용자별 SseEmitter 객체를 저장하는 맵
    private final Map<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    // 여러 사용자에게 알림을 보내는 메서드
    public void sendNotificationsToUsers(List<AlarmDto.Request> requests) {
        for (AlarmDto.Request request : requests) {
            sendNotification(request);
        }
    }

    // 개별 사용자에게 알림을 보내는 메서드
    private void sendNotification(AlarmDto.Request request) {
        SseEmitter emitter = emitterMap.get(request.getReceiverId());

        String alarmMessage = "";
        switch(request.getAlarm_type()) {
            case "A1" :
                alarmMessage = customerRepository.findById(request.getSenderId()).get().getCustomerName()
                        + "님에게서 견적 요청서가 도착했습니다.";
            case "A2" :
                alarmMessage = designerRepository.findById(request.getSenderId()).get().getOfficialName()
                        + "에서 견적서가 도착했습니다.";
            case "A3" :
                alarmMessage = customerRepository.findById(request.getSenderId()).get().getCustomerName()
                        + "님에게서 예약 요청이 도착했습니다.";
            case "A4" :
                alarmMessage = customerRepository.findById(request.getSenderId()).get().getCustomerName()
                        + "님이 리뷰를 등록했습니다.";
        }

        // 알림을 DB에 저장 (연결이 되어 있든 아니든)
        saveNotification(request, alarmMessage);

        if (emitter != null) {
            try {
                emitter.send(alarmMessage);
            } catch (Exception e) {
                emitterMap.remove(request.getReceiverId()); // 실패 시 해당 사용자 제거
            }
        }
    }

    @Transactional
    public void saveNotification(AlarmDto.Request request, String alarmMessage) {

        Alarm alarm = Alarm.builder()
                .senderId(request.getSenderId())
                .senderType(request.getSenderType())
                .receiverId(request.getReceiverId())
                .receiverType(request.getReceiverType())
                .alarm_message(alarmMessage)
                .alarm_type(request.getAlarm_type()) // 알림 유형 예시
                .alarm_status(false) // 기본 상태는 '읽지 않음'
                .build();

        alarmRepository.save(alarm);
    }

    // 사용자에게 읽지 않은 알림을 가져오는 메서드
    public List<Alarm> getUnreadNotifications(Long receiverId) {
        // UNREAD 상태의 알림을 조회
        return alarmRepository.findByReceiverIdAndAlarmStatus(receiverId, false);
    }

    // 알림을 읽음 상태로 업데이트
    @Transactional
    public void markNotificationsAsRead(Long receiverId) {
        List<Alarm> unreadAlarms = alarmRepository.findByReceiverIdAndAlarmStatus(receiverId, false);
        for (Alarm alarm : unreadAlarms) {
            alarm.toBuilder().alarm_status(true).build();
            alarmRepository.save(alarm);
        }
    }

    // SseEmitter에 접근하는 메서드
    public Map<Long, SseEmitter> getEmitterMap() {
        return emitterMap;
    }
}
