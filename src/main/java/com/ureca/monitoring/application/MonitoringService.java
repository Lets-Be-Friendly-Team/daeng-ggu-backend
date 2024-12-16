package com.ureca.monitoring.application;

import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.monitoring.domain.Process;
import com.ureca.monitoring.domain.ProcessStatus;
import com.ureca.monitoring.infrastructure.ProcessRepository;
import com.ureca.monitoring.presentaion.dto.*;
import com.ureca.monitoring.presentaion.dto.ReservationInfoForDesignerDto;
import com.ureca.profile.domain.Pet;
import com.ureca.profile.infrastructure.CommonCodeRepository;
import com.ureca.reservation.domain.Reservation;
import com.ureca.reservation.infrastructure.ReservationRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MonitoringService {

    private final ReservationRepository reservationRepository;
    private final ProcessRepository processRepository;
    private final CommonCodeRepository commonCodeRepository;

    // 보호자
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

        // majorBreedCode 및 subBreedCode를 codeDesc로 변환
        String majorBreedDesc = commonCodeRepository.findCodeDescByCodeId(pet.getMajorBreedCode());
        String subBreedDesc = commonCodeRepository.findCodeDescByCodeId(pet.getSubBreedCode());

        // PetInfoDto 생성
        PetInfoDto petInfo =
                PetInfoDto.builder()
                        .petName(pet.getPetName())
                        .birthDate(pet.getBirthDate().toString())
                        .gender(pet.getGender())
                        .weight(pet.getWeight())
                        .specialNotes(pet.getSpecialNotes())
                        .isNeutered("Y".equalsIgnoreCase(pet.getIsNeutered())) // N/Y 처리
                        .majorBreed(majorBreedDesc)
                        .subBreed(subBreedDesc)
                        .build();

        // ProcessStatusDto 생성
        ProcessStatusDto processStatus =
                process != null
                        ? ProcessStatusDto.builder()
                                .isDelivery(reservation.getIsDelivery())
                                .processNum(process.getProcessNum())
                                .processStatus(process.getProcessStatus().name())
                                .processMessage(process.getProcessMessage())
                                .build()
                        : null;

        // ReservationInfoForDesignerDto 생성
        return ReservationInfoForDesignerDto.builder()
                .customerPhone(reservation.getPet().getCustomer().getPhone())
                .customerName(reservation.getPet().getCustomer().getCustomerName())
                .petInfo(petInfo)
                .status(processStatus)
                .build();
    }

    /**
     * 디자이너가 스트리밍을 시작하는 메서드
     *
     * @param reservationId 예약 ID
     * @return StreamingDto
     */
    @Transactional
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

        // 3. Process 업데이트 (기존 객체를 수정)
        process.updateStatus(
                process.getProcessNum() + 1, // 기존 단계에 +1
                ProcessStatus.GROOMING, // 상태 변경
                ProcessStatus.GROOMING.getDescription() // 새로운 상태 메시지
                );
        process.updateStreamValue(
                "스트리밍 URL", // TODO: 스트리밍 URL 생성
                "스트리밍 KEY" // TODO: 스트림 키 설정
                );

        // 업데이트된 Process 저장
        process = processRepository.save(process);

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

    @Transactional
    public ProcessStatusDto endStreaming(Long reservationId) {
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

        // 3. isDelivery 여부에 따라 Process 상태 업데이트
        if (Boolean.TRUE.equals(reservation.getIsDelivery())) {
            // 배송이 필요한 경우
            process.updateStatus(
                    process.getProcessNum() + 1, // 기존 단계에 +1
                    ProcessStatus.WAITING_FOR_DELIVERY, // 상태: 배송 대기 중
                    ProcessStatus.WAITING_FOR_DELIVERY.getDescription() // 상태 메시지
                    );
        } else {
            // 배송이 필요 없는 경우
            process.updateStatus(
                    process.getProcessNum() + 1, // 기존 단계에 +1
                    ProcessStatus.COMPLETED, // 상태: 서비스 완료
                    ProcessStatus.COMPLETED.getDescription() // 상태 메시지
                    );
        }
        // 4. 스트리밍 관련 정보 제거
        process.updateStreamValue(null, null);

        // 5. Process 저장
        processRepository.save(process);

        // 6. ProcessStatusDto 생성 및 반환
        return ProcessStatusDto.builder()
                .isDelivery(
                        reservation.getIsDelivery() != null
                                ? reservation.getIsDelivery()
                                : false) // 배송 여부
                .processNum(process.getProcessNum())
                .processStatus(process.getProcessStatus().name())
                .processMessage(process.getProcessMessage())
                .build();
    }

    // 가디언

    @Transactional(readOnly = true)
    public List<ReservationInfoForGuardianDto> getUpcomingDeliveryReservations() {
        // 1. isDelivery가 true, isFinished가 false인 예약을 조회하고 날짜와 시간 기준으로 정렬
        List<Reservation> reservations =
                reservationRepository
                        .findByIsDeliveryTrueAndIsFinishedFalseOrderByReservationDateAscStartTimeAsc();

        // 2. Reservation 리스트를 ReservationInfoForGuardianDto로 변환
        return reservations.stream()
                .map(
                        reservation -> {
                            // 반려견 정보 생성
                            Pet pet = reservation.getPet();

                            String majorBreedDesc =
                                    commonCodeRepository.findCodeDescByCodeId(
                                            pet.getMajorBreedCode());
                            String subBreedDesc =
                                    commonCodeRepository.findCodeDescByCodeId(
                                            pet.getSubBreedCode());

                            PetInfoDto petInfo =
                                    PetInfoDto.builder()
                                            .petName(pet.getPetName())
                                            .petImgUrl(pet.getPetImgUrl())
                                            .birthDate(
                                                    pet.getBirthDate() != null
                                                            ? pet.getBirthDate().toString()
                                                            : null)
                                            .gender(pet.getGender())
                                            .weight(pet.getWeight())
                                            .specialNotes(pet.getSpecialNotes())
                                            .isNeutered(
                                                    "Y"
                                                            .equalsIgnoreCase(
                                                                    pet
                                                                            .getIsNeutered())) // N/Y 처리 // 결과: true // 중성화 여부
                                            .majorBreed(majorBreedDesc)
                                            .subBreed(subBreedDesc)
                                            .build();

                            // 예약 정보 DTO 생성
                            return ReservationInfoForGuardianDto.builder()
                                    .reservationId(reservation.getReservationId())
                                    .reservationDate(reservation.getReservationDate())
                                    .startTime(
                                            reservation
                                                    .getStartTime()
                                                    .getHour()) // LocalTime에서 시간 추출
                                    .isFinished(reservation.getIsFinished())
                                    .processId(
                                            reservation.getProcess() != null
                                                    ? reservation.getProcess().getProcessId()
                                                    : null)
                                    .customerAddress(
                                            reservation
                                                    .getPet()
                                                    .getCustomer()
                                                    .getAddress2()) // 보호자 주소
                                    .shopAddress(
                                            reservation.getDesigner().getAddress2()) // 디자이너의 샵 주소
                                    .petInfo(petInfo)
                                    .build();
                        })
                .collect(Collectors.toList());
    }
}
