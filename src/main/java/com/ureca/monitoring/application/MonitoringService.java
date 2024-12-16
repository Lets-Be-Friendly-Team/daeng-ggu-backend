package com.ureca.monitoring.application;

import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.monitoring.domain.Process;
import com.ureca.monitoring.domain.ProcessStatus;
import com.ureca.monitoring.infrastructure.ProcessRepository;
import com.ureca.monitoring.presentaion.dto.*;
import com.ureca.monitoring.presentaion.dto.ReservationInfoForDesignerDto;
import com.ureca.profile.domain.Pet;
import com.ureca.reservation.domain.Reservation;
import com.ureca.reservation.infrastructure.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MonitoringService {

    private final ReservationRepository reservationRepository;
    private final ProcessRepository processRepository;

    /**
     * 예약 정보를 조회하여 ReservationInfoForDesignerDto로 변환하는 메서드
     *
     * @param reservationId 예약 ID
     * @return ReservationInfoForDesignerDto
     */
    @Transactional(readOnly = true)
    public ReservationInfoForDesignerDto getReservationInfo(Long reservationId) {
        // 예약 정보 조회
        Reservation reservation =
                reservationRepository
                        .findById(reservationId)
                        .orElseThrow(() -> new ApiException(ErrorCode.RESERVATION_NOT_EXIST));

        // 반려견 정보 가져오기
        Pet pet = reservation.getPet();

        // Process 데이터 가져오기
        Process process = reservation.getProcess();

        // PetInfoDto 생성
        PetInfoDto petInfo =
                PetInfoDto.builder()
                        .petName(pet.getPetName())
                        .birthDate(pet.getBirthDate().toString())
                        .gender(pet.getGender())
                        .weight(pet.getWeight())
                        .specialNotes(pet.getSpecialNotes())
                        .isNeutered(
                                pet.getIsNeutered() != null
                                        && pet.getIsNeutered().equalsIgnoreCase("true"))
                        .majorBreed(pet.getMajorBreedCode())
                        .subBreed(pet.getSubBreedCode())
                        .build();

        // ProcessStatusDto 생성
        ProcessStatusDto processStatus =
                process != null
                        ? ProcessStatusDto.builder()
                                .isDelivery(false) // Process에서 필요시 추가 정보 설정 가능
                                .processNum(process.getProcessNum())
                                .processStatus(process.getProcessStatus().name())
                                .processMessage(process.getProcessMessage())
                                .build()
                        : null;

        // ReservationInfoForDesignerDto 생성
        return ReservationInfoForDesignerDto.builder()
                .customerPhone(reservation.getPet().getCustomer().getPhone()) // 고객 전화번호
                .customerName(reservation.getPet().getCustomer().getCustomerName()) // 고객 이름
                .petInfo(petInfo) // 반려견 정보
                .status(processStatus) // 진행 상태
                .build();
    }

    /**
     * 디자이너가 스트리밍을 시작하는 메서드
     *
     * @param reservationId 예약 ID
     * @return StreamingDto
     */
    public StreamingDto designerStartStreaming(Long reservationId) {
        // 1. Reservation 조회
        Reservation reservation =
                reservationRepository
                        .findById(reservationId)
                        .orElseThrow(() -> new ApiException(ErrorCode.RESERVATION_NOT_EXIST));

        // 2. Process 조회
        Process process = reservation.getProcess();
        if (process == null) {
            throw new ApiException(ErrorCode.PROCESS_NOT_STARTED);
        }

        // 3. Process 업데이트
        Process updateProcess =
                Process.builder()
                        .processNum(process.getProcessNum() + 1) // 기존 단계에 +1
                        .customerId(process.getCustomerId())
                        .processStatus(ProcessStatus.GROOMING) // 상태 변경
                        .processMessage(ProcessStatus.GROOMING.getDescription()) // 새로운 상태 메시지
                        .streamKey(generateStreamKey()) // 스트림 키 설정
                        .playbackUrl(generateStreamUrl(generateStreamKey())) // 스트리밍 URL 생성
                        .build();

        // 업데이트된 Process를 저장
        process = processRepository.save(updateProcess);

        // 4. ProcessStatusDto 생성
        ProcessStatusDto processStatusDto =
                ProcessStatusDto.builder()
                        .isDelivery(
                                reservation.getIsDelivery() != null
                                        ? reservation.getIsDelivery()
                                        : false) // 배송 여부
                        .processNum(process.getProcessNum())
                        .processStatus(process.getProcessStatus().name())
                        .processMessage(process.getProcessMessage())
                        .build();

        // 5. StreamingDto 생성 및 반환
        return StreamingDto.builder()
                .reservationId(reservationId)
                .streamKey(process.getStreamKey())
                .streamUrl(process.getPlaybackUrl())
                .statusDto(processStatusDto)
                .build();
    }

    // TODO: 스트리밍에 필요한 값 호출 메서드 붙여야 함
    private String generateStreamKey() {
        return null;
    }

    private String generateStreamUrl(String streamKey) {
        return null;
    }
}
