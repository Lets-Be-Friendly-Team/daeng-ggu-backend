package com.ureca.monitoring.presentaion;

import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.monitoring.presentaion.dto.PetInfoDto;
import com.ureca.monitoring.presentaion.dto.ProcessStatusDto;
import com.ureca.monitoring.presentaion.dto.ReservationInfoForDesignerDto;
import com.ureca.monitoring.presentaion.dto.StreamingDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/daengggu/designer")
@RequiredArgsConstructor
@Tag(name = "Designer API In Monitoring", description = "모니터링에서 디자이너 관련 api")
public class MonitoringDesignerController {

    /**
     * 미용실이 시작 전 고객 정보를 확인하는 API.
     *
     * @param reservationId 예약 고유 ID
     * @return 고객 및 반려견 정보 + 진행 상태 DTO
     */
    @GetMapping("/process/{reservationId}/info")
    @Operation(summary = "시작 전 고객 정보 조회", description = "미용 시작 전 고객과 반려견 정보를 조회합니다.")
    public ResponseDto<ReservationInfoForDesignerDto> getProcessInfo(
            @PathVariable Long reservationId) {

        // PetInfoDto 더미 데이터 생성
        PetInfoDto petInfo =
                PetInfoDto.builder()
                        .petName("멍멍이")
                        .birthDate("2023-01-01")
                        .gender("Male")
                        .weight(5.5)
                        .specialNotes("특이사항 없음")
                        .isNeutered(true)
                        .majorBreed("골든 리트리버")
                        .subBreed("미니 리트리버")
                        .build();

        // ProcessStatusDto 더미 데이터 생성
        ProcessStatusDto status =
                ProcessStatusDto.builder()
                        .isDelivery(true)
                        .processNum(1)
                        .processStatus("PREPARING")
                        .processMessage("시작 전 서비스 준비 중.")
                        .build();

        // ReservationInfoForDesignerDto 더미 데이터 생성
        ReservationInfoForDesignerDto info =
                ReservationInfoForDesignerDto.builder()
                        .customerPhone("010-1234-5678")
                        .customerName("김고객")
                        .petInfo(petInfo)
                        .status(status)
                        .build();

        return ResponseUtil.SUCCESS("고객 정보 조회 성공", info);
    }

    /**
     * 미용실이 스트리밍 시작을 위한 API.
     *
     * @return 스트리밍 URL, 스트림 키 + 진행 상태 DTO
     */
    @PostMapping("/process/{reservationId}/start")
    @Operation(summary = "스트리밍 시작", description = "미용실이 스트리밍을 시작합니다.")
    public ResponseDto<StreamingDto> startStreaming(@PathVariable Long reservationId) {
        return ResponseUtil.SUCCESS(
                "스트리밍 시작",
                StreamingDto.builder()
                        .streamUrl("http://streaming.example.com/live/1234")
                        .streamKey("abcd-efgh-1234")
                        .statusDto(
                                ProcessStatusDto.builder()
                                        .isDelivery(true)
                                        .processNum(4)
                                        .processStatus("GROOMING")
                                        .processMessage("미용 진행 중.")
                                        .build())
                        .build());
    }
    ;

    /**
     * 미용실이 스트리밍 종료를 위한 API.
     *
     * @return 진행 상태 DTO
     */
    @PostMapping("/process/{reservationId}/end")
    @Operation(summary = "스트리밍 종료", description = "미용실이 스트리밍을 종료합니다.")
    public ResponseDto<ProcessStatusDto> endProcess(@PathVariable Long reservationId) {
        return ResponseUtil.SUCCESS(
                "스트리밍 종료",
                ProcessStatusDto.builder()
                        .isDelivery(true)
                        .processNum(5)
                        .processStatus("WAITING_FOR_DELIVERY")
                        .processMessage("미용 종료 후 배송 대기")
                        .build());
    }
}
