package com.ureca.alarm.application;

import com.ureca.alarm.domain.Alarm;
import com.ureca.alarm.infrastructure.AlarmRepository;
import com.ureca.alarm.presentation.dto.AlarmDto;
import com.ureca.estimate.infrastructure.EstimateRepository;
import com.ureca.profile.infrastructure.CustomerRepository;
import com.ureca.profile.infrastructure.DesignerRepository;
import com.ureca.request.infrastructure.RequestRepository;
import com.ureca.reservation.infrastructure.ReservationRepository;
import com.ureca.review.domain.Enum.AuthorType;
import com.ureca.review.infrastructure.ReviewRepository;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
    private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    // 여러 사용자에게 알림을 보내는 메서드
    public void sendNotificationsToUsers(List<AlarmDto.Request> requests) {
        for (AlarmDto.Request request : requests) {
            sendNotification(request);
        }
    }

    // 개별 사용자에게 알림을 보내는 메서드
    private void sendNotification(AlarmDto.Request request) {
        SseEmitter emitter =
                emitterMap.get(request.getReceiverType().name() + request.getReceiverId());

        String alarmMessage = "";
        switch (request.getAlarmType()) {
            case "A1":
                alarmMessage =
                        customerRepository
                                .findById(request.getSenderId())
                                .map(
                                        customer ->
                                                customer.getCustomerName() + "님에게서 견적 요청서가 도착했습니다.")
                                .orElse("알림 메시지 오류");
                break;
            case "A2":
                alarmMessage =
                        designerRepository
                                .findById(request.getSenderId())
                                .map(designer -> designer.getOfficialName() + "에서 견적서가 도착했습니다.")
                                .orElse("알림 메시지 오류");
                break;
            case "A3":
                alarmMessage =
                        customerRepository
                                .findById(request.getSenderId())
                                .map(customer -> customer.getCustomerName() + "님에게서 예약 요청이 도착했습니다.")
                                .orElse("알림 메시지 오류");
                break;
            case "A4":
                alarmMessage =
                        customerRepository
                                .findById(request.getSenderId())
                                .map(customer -> customer.getCustomerName() + "님이 리뷰를 등록했습니다.")
                                .orElse("알림 메시지 오류");
                break;
            default:
                alarmMessage = "알림 유형이 잘못되었습니다.";
                break;
        }

        // 알림을 DB에 저장 (연결이 되어 있든 아니든)
        saveNotification(request, alarmMessage);

        if (emitter != null) {
            try {
                emitter.send(alarmMessage);
            } catch (Exception e) {
                emitterMap.remove(
                        request.getReceiverType().name()
                                + String.valueOf(request.getReceiverId())); // 실패 시 해당 사용자 제거
            }
        }
    }

    @Transactional
    public void saveNotification(AlarmDto.Request request, String alarmMessage) {

        Alarm alarm =
                Alarm.builder()
                        .senderId(request.getSenderId())
                        .senderType(request.getSenderType())
                        .receiverId(request.getReceiverId())
                        .receiverType(request.getReceiverType())
                        .alarmMessage(alarmMessage)
                        .alarmType(request.getAlarmType()) // 알림 유형 예시
                        .alarmStatus(false) // 기본 상태는 '읽지 않음'
                        .build();

        alarmRepository.save(alarm);
    }

    // 읽음 처리 메소드
    public Alarm getUnreadToRead(Long alarmId) {
        // UNREAD 상태의 알림을 조회
        Alarm alarm = alarmRepository.findById(alarmId).get();
        alarm.toBuilder().alarmStatus(true).build();
        return alarm;
    }

    public List<AlarmDto.Response> getAlarmsByReceiver(
            Long receiverId, AuthorType receiverType, int page) {
        // 페이지 요청 생성 (10개 고정)
        Pageable pageable = PageRequest.of(page, 10);

        // Repository에서 데이터 조회 후 Dto로 변환
        return alarmRepository
                .findByReceiverIdAndReceiverType(receiverId, receiverType, pageable)
                .stream()
                .map(AlarmDto.Response::fromEntity) // Entity -> Response DTO 변환
                .collect(Collectors.toList());
    }

    // SseEmitter에 접근하는 메서드
    public Map<String, SseEmitter> getEmitterMap() {
        return emitterMap;
    }
}
