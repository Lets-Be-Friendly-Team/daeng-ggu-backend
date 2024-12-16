package com.ureca.monitoring.presentaion;

import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.monitoring.application.MonitoringService;
import com.ureca.monitoring.presentaion.dto.PetInfoDto;
import com.ureca.monitoring.presentaion.dto.ProcessStatusDto;
import com.ureca.monitoring.presentaion.dto.ReservationInfoForGuardianDto;
import com.ureca.monitoring.presentaion.dto.StreamingDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/daengggu/guardian")
@RequiredArgsConstructor
@Tag(name = "Guardian API In Monitoring", description = "모니터링에서 배달기사 관련 API")
public class MonitoringGuardianController {

    private final MonitoringService monitoringService;

    /**
     * 배달기사가 전체 예약 리스트를 조회하는 API.
     *
     * @return 예약 리스트
     */
    @GetMapping("/reservations")
    @Operation(summary = "예약 리스트 조회", description = "배달기사가 전체 예약 리스트를 조회하는 API.")
    public ResponseDto<List<ReservationInfoForGuardianDto>> getReservationList() {
        return ResponseUtil.SUCCESS(
                "예약 리스트 조회 성공", monitoringService.getUpcomingDeliveryReservations());
    }

    /**
     * 배달기사가 특정 예약을 선택하여 프로세스를 생성하는 API.
     *
     * @return 진행 상태 DTO
     */
    @PostMapping("/reservation/{reservationId}/process")
    @Operation(
            summary = "가디언이 버튼을 누르면 예약 프로세스 생성",
            description = "배달기사가 특정 예약을 선택하여 프로세스를 생성하는 API.")
    public ResponseDto<ProcessStatusDto> createProcess(@PathVariable Long reservationId) {
        return ResponseUtil.SUCCESS(
                "프로세스 생성 성공",
                ProcessStatusDto.builder()
                        .isDelivery(true)
                        .processNum(1)
                        .processStatus("PREPARING")
                        .processMessage("시작 전 서비스 준비 중.")
                        .build());
    }

    /**
     * 배달기사가 단일 예약 정보를 조회하는 API.
     *
     * @return 예약 정보
     */
    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "특정 예약 정보 조회", description = "배달기사가 예약 한개의 정보를 조회하는 API.")
    public ResponseDto<ReservationInfoForGuardianDto> getReservationInfo() {
        PetInfoDto petInfo =
                PetInfoDto.builder()
                        .petName("초코")
                        .petImgUrl(
                                "https://letsbefriendly.s3.ap-northeast-2.amazonaws.com/janggun.jpg")
                        .birthDate("2022-05-10")
                        .gender("Female")
                        .weight(6.0)
                        .specialNotes("알레르기 있음")
                        .isNeutered(true)
                        .majorBreed("포메라니안")
                        .subBreed("스피츠계열")
                        .build();

        ReservationInfoForGuardianDto reservation =
                ReservationInfoForGuardianDto.builder()
                        .reservationId(1L)
                        .reservationDate(LocalDate.of(2024, 12, 15))
                        .startTime(10)
                        .isFinished(false)
                        .processId(1L)
                        .customerAddress("서울 강남구 테헤란로 123")
                        .shopAddress("경기 성남시 분당구 판교로 456")
                        .petInfo(petInfo)
                        .build();

        return ResponseUtil.SUCCESS("예약 정보 조회 성공", reservation);
    }

    /**
     * 배달기사가 미용실로 배송 시작을 위한 API.
     *
     * @return 스트리밍 DTO
     */
    @PostMapping("/process/{reservationId}/start-delivery-to-shop")
    @Operation(summary = "배송 시작을 눌러 미용실로 배송 시작", description = "배달기사가 미용실로 배송 시작을 위한 API.")
    public ResponseDto<StreamingDto> startDeliveryToShop(@PathVariable Long reservationId) {
        return ResponseUtil.SUCCESS(
                "배송 시작",
                StreamingDto.builder()
                        .streamUrl("http://streaming.example.com/live/shop123")
                        .streamKey("key-1234-5678")
                        .statusDto(
                                ProcessStatusDto.builder()
                                        .isDelivery(true)
                                        .processNum(2)
                                        .processStatus("DELIVERY_TO_SHOP")
                                        .processMessage("고객 집에서 미용실까지 배송 중.")
                                        .build())
                        .build());
    }

    /**
     * 배달기사가 미용실 도착 버튼을 누를 때 상태 변경 API.
     *
     * @return 진행 상태 DTO
     */
    @PostMapping("/process/{reservationId}/arrive-at-shop")
    @Operation(summary = "도착 버튼을 눌러 미용실 도착", description = "배달기사가 미용실 도착 버튼을 누를 때 상태 변경 API")
    public ResponseDto<ProcessStatusDto> arriveAtShop(@PathVariable Long reservationId) {
        return ResponseUtil.SUCCESS(
                "미용실 도착",
                ProcessStatusDto.builder()
                        .processNum(3)
                        .processStatus("WAITING_FOR_GROOMING")
                        .processMessage("미용 시작 전 대기.")
                        .build());
    }

    /**
     * 배달기사가 집으로 배송 시작을 위한 API.
     *
     * @return 스트리밍 DTO
     */
    @PostMapping("/process/{reservationId}/start-delivery-to-home")
    @Operation(summary = "배송 시작을 눌러 집으로 배송 시작", description = "배달기사가 집으로 배송 시작을 위한 API.")
    public ResponseDto<StreamingDto> startDeliveryToHome(@PathVariable Long reservationId) {
        return ResponseUtil.SUCCESS(
                "집으로 배송 시작",
                StreamingDto.builder()
                        .streamUrl("http://streaming.example.com/live/home123")
                        .streamKey("key-9876-5432")
                        .statusDto(
                                ProcessStatusDto.builder()
                                        .isDelivery(true)
                                        .processNum(6)
                                        .processStatus("DELIVERY_TO_HOME")
                                        .processMessage("미용실에서 고객 집으로 배송 중.")
                                        .build())
                        .build());
    }

    /**
     * 배달기사가 집 도착 버튼을 누를 때 상태 변경 API.
     *
     * @return 진행 상태 DTO
     */
    @PostMapping("/process/{reservationId}/arrive-at-home")
    @Operation(summary = "배송완료를 눌러 집 도착", description = "배달기사가 집 도착 버튼을 누를 때 상태 변경 API.")
    public ResponseDto<ProcessStatusDto> arriveAtHome(@PathVariable Long reservationId) {
        return ResponseUtil.SUCCESS(
                "집 도착",
                ProcessStatusDto.builder()
                        .isDelivery(true)
                        .processNum(7)
                        .processStatus("COMPLETED")
                        .processMessage("서비스 완료.")
                        .build());
    }
}
