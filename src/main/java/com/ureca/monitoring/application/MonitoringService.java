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

    // 공통 메서드: 예약 조회
    private Reservation getReservation(Long reservationId) {
        return reservationRepository
                .findById(reservationId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESERVATION_NOT_EXIST));
    }

    // 공통 메서드: 프로세스 조회
    private Process getProcess(Reservation reservation) {
        Process process = reservation.getProcess();
        if (process == null) {
            throw new ApiException(ErrorCode.PROCESS_NOT_STARTED);
        }
        return process;
    }

    // 공통 메서드: 프로세스 상태 DTO 생성
    private ProcessStatusDto createProcessStatusDto(Process process, Boolean isDelivery) {
        return ProcessStatusDto.builder()
                .isDelivery(isDelivery != null ? isDelivery : false)
                .processNum(process.getProcessNum())
                .processStatus(process.getProcessStatus().name())
                .processMessage(process.getProcessMessage())
                .build();
    }

    // 공통 메서드: 프로세스 상태 및 메시지 업데이트 후 저장
    private void updateProcessAndSave(Process process, ProcessStatus status, String description) {
        process.updateStatus(
                process.getProcessNum() + 1, // 무조건 +1
                status,
                description);
        processRepository.save(process);
    }

    // 공통 메서드: 스트리밍 값 업데이트 후 저장
    private void updateStreamAndSave(Process process, String streamUrl, String streamKey) {
        process.updateStreamValue(streamUrl, streamKey);
        processRepository.save(process);
    }

    // 공통 메서드: PetInfoDto 생성
    private PetInfoDto createPetInfoDto(Pet pet) {
        // majorBreedCode 및 subBreedCode를 codeDesc로 변환
        String majorBreedDesc = getCodeDescription(pet.getMajorBreedCode());
        String subBreedDesc = getCodeDescription(pet.getSubBreedCode());

        // PetInfoDto 생성
        return PetInfoDto.builder()
                .petName(pet.getPetName())
                .petImgUrl(pet.getPetImgUrl())
                .birthDate(pet.getBirthDate() != null ? pet.getBirthDate().toString() : null)
                .gender(pet.getGender())
                .weight(pet.getWeight())
                .specialNotes(pet.getSpecialNotes())
                .isNeutered("Y".equalsIgnoreCase(pet.getIsNeutered())) // N/Y 처리
                .majorBreed(majorBreedDesc)
                .subBreed(subBreedDesc)
                .build();
    }

    // 공통 메서드: 공통 코드 설명 조회
    private String getCodeDescription(String codeId) {
        return codeId != null ? commonCodeRepository.findCodeDescByCodeId(codeId) : null;
    }

    // TODO: 스트리밍에 필요한 값 호출 메서드 붙여야 함
    private String generateStreamKey() {
        return null;
    }

    // TODO: 스트리밍에 필요한 값 호출 메서드 붙여야 함
    private String generateStreamUrl(String streamKey) {
        return null;
    }

    // 디자이너 API
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

        // PetInfoDto 생성 (공통 메서드 사용)
        PetInfoDto petInfo = createPetInfoDto(pet);

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
        // 예약 및 프로세스 조회
        Reservation reservation = getReservation(reservationId);
        Process process = getProcess(reservation);

        // 프로세스 상태 업데이트
        updateProcessAndSave(
                process, ProcessStatus.GROOMING, ProcessStatus.GROOMING.getDescription());

        // 스트리밍 정보 생성
        String streamKey = "스트리밍 KEY"; // TODO: 스트리밍 키 생성 로직
        String streamUrl = "스트리밍 URL"; // TODO: 스트리밍 URL 생성 로직
        updateStreamAndSave(process, streamUrl, streamKey);

        // ProcessStatusDto 생성
        ProcessStatusDto processStatusDto =
                createProcessStatusDto(process, reservation.getIsDelivery());

        // StreamingDto 반환
        return StreamingDto.builder()
                .reservationId(reservationId)
                .streamKey(process.getStreamKey())
                .streamUrl(process.getPlaybackUrl())
                .statusDto(processStatusDto)
                .build();
    }

    @Transactional
    public ProcessStatusDto designerEndStreaming(Long reservationId) {
        // 예약 및 프로세스 조회
        Reservation reservation = getReservation(reservationId);
        Process process = getProcess(reservation);

        // 배송 여부에 따라 프로세스 상태 업데이트
        if (Boolean.TRUE.equals(reservation.getIsDelivery())) {
            updateProcessAndSave(
                    process,
                    ProcessStatus.WAITING_FOR_DELIVERY,
                    ProcessStatus.WAITING_FOR_DELIVERY.getDescription());
        } else {
            updateProcessAndSave(
                    process, ProcessStatus.COMPLETED, ProcessStatus.COMPLETED.getDescription());
        }

        // 스트리밍 정보 제거
        updateStreamAndSave(process, null, null);

        // ProcessStatusDto 생성
        return createProcessStatusDto(process, reservation.getIsDelivery());
    }

    // 가디언 API

    /**
     * 배달기사가 예약 리스트를 조회하는 메서드
     *
     * @return List<ReservationInfoForGuardianDto>
     */
    @Transactional(readOnly = true)
    public List<ReservationInfoForGuardianDto> getUpcomingDeliveryReservations() {
        // 예약 조회
        List<Reservation> reservations =
                reservationRepository
                        .findByIsDeliveryTrueAndIsFinishedFalseOrderByReservationDateAscStartTimeAsc();

        // 변환
        return reservations.stream()
                .map(
                        reservation -> {
                            Pet pet = reservation.getPet();
                            PetInfoDto petInfo = createPetInfoDto(pet);

                            return ReservationInfoForGuardianDto.builder()
                                    .reservationId(reservation.getReservationId())
                                    .reservationDate(reservation.getReservationDate())
                                    .startTime(reservation.getStartTime().getHour())
                                    .isFinished(reservation.getIsFinished())
                                    .processId(
                                            reservation.getProcess() != null
                                                    ? reservation.getProcess().getProcessId()
                                                    : null)
                                    .customerAddress(
                                            reservation.getPet().getCustomer().getAddress2())
                                    .shopAddress(reservation.getDesigner().getAddress2())
                                    .petInfo(petInfo)
                                    .build();
                        })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReservationInfoForGuardianDto getGuardianReservationInfo(Long reservationId) {
        // 예약 조회
        Reservation reservation = getReservation(reservationId);

        // PetInfoDto 생성 (공통 메서드 사용)
        PetInfoDto petInfo = createPetInfoDto(reservation.getPet());

        // ReservationInfoForGuardianDto 생성
        return ReservationInfoForGuardianDto.builder()
                .reservationId(reservation.getReservationId())
                .reservationDate(reservation.getReservationDate())
                .startTime(reservation.getStartTime().getHour())
                .isFinished(reservation.getIsFinished())
                .processId(
                        reservation.getProcess() != null
                                ? reservation.getProcess().getProcessId()
                                : null)
                .customerAddress(reservation.getPet().getCustomer().getAddress2())
                .shopAddress(reservation.getDesigner().getAddress2())
                .petInfo(petInfo)
                .build();
    }

    @Transactional
    public StreamingDto startDeliveryToShop(Long reservationId) {
        // 예약 및 프로세스 조회
        Reservation reservation = getReservation(reservationId);
        Process process = getProcess(reservation);

        // 프로세스 상태 업데이트
        updateProcessAndSave(
                process,
                ProcessStatus.DELIVERY_TO_SHOP,
                ProcessStatus.DELIVERY_TO_SHOP.getDescription());

        // 스트리밍 정보 생성
        String streamKey = "스트리밍 KEY"; // TODO: 스트리밍 키 생성 로직
        String streamUrl = "스트리밍 URL"; // TODO: 스트리밍 URL 생성 로직
        updateStreamAndSave(process, streamUrl, streamKey);

        // ProcessStatusDto 생성
        ProcessStatusDto processStatusDto =
                createProcessStatusDto(process, reservation.getIsDelivery());

        // StreamingDto 반환
        return StreamingDto.builder()
                .reservationId(reservationId)
                .streamKey(process.getStreamKey())
                .streamUrl(process.getPlaybackUrl())
                .statusDto(processStatusDto)
                .build();
    }

    @Transactional
    public StreamingDto startDeliveryToHome(Long reservationId) {
        // 예약 및 프로세스 조회
        Reservation reservation = getReservation(reservationId);
        Process process = getProcess(reservation);

        // 프로세스 상태 업데이트
        updateProcessAndSave(
                process,
                ProcessStatus.DELIVERY_TO_HOME,
                ProcessStatus.DELIVERY_TO_HOME.getDescription());

        // 스트리밍 정보 생성
        String streamKey = "스트리밍 KEY"; // TODO: 스트리밍 키 생성 로직
        String streamUrl = "스트리밍 URL"; // TODO: 스트리밍 URL 생성 로직
        updateStreamAndSave(process, streamUrl, streamKey);

        // ProcessStatusDto 생성
        ProcessStatusDto processStatusDto =
                createProcessStatusDto(process, reservation.getIsDelivery());

        // StreamingDto 반환
        return StreamingDto.builder()
                .reservationId(reservationId)
                .streamKey(process.getStreamKey())
                .streamUrl(process.getPlaybackUrl())
                .statusDto(processStatusDto)
                .build();
    }

    @Transactional
    public ProcessStatusDto arriveAtHome(Long reservationId) {
        // 예약 및 프로세스 조회
        Reservation reservation = getReservation(reservationId);
        Process process = getProcess(reservation);

        // 프로세스 상태 업데이트: 서비스 완료
        updateProcessAndSave(
                process, ProcessStatus.COMPLETED, ProcessStatus.COMPLETED.getDescription());

        // ProcessStatusDto 반환
        return createProcessStatusDto(process, reservation.getIsDelivery());
    }

    @Transactional
    public ProcessStatusDto arriveAtShop(Long reservationId) {
        // 예약 및 프로세스 조회
        Reservation reservation = getReservation(reservationId);
        Process process = getProcess(reservation);

        // 프로세스 상태 업데이트: 미용 시작 전 대기
        updateProcessAndSave(
                process,
                ProcessStatus.WAITING_FOR_GROOMING,
                ProcessStatus.WAITING_FOR_GROOMING.getDescription());

        // ProcessStatusDto 반환
        return createProcessStatusDto(process, reservation.getIsDelivery());
    }

    @Transactional
    public ProcessStatusDto createProcess(Long reservationId) {
        // 1. 예약 조회
        Reservation reservation = getReservation(reservationId);

        // 2. 이미 프로세스가 있는 경우 예외 처리
        if (reservation.getProcess() != null) {
            throw new ApiException(ErrorCode.PROCESS_ALREADY_EXISTS);
        }

        // 3. 새로운 프로세스 생성
        Process newProcess =
                Process.builder()
                        .guardian(null) // TODO: 가디언 user 관리 기능 이후 추가 처리
                        .customerId(reservation.getPet().getCustomer().getCustomerId())
                        .processNum(1) // 초기 상태 번호
                        .processStatus(ProcessStatus.PREPARING) // 초기 상태
                        .processMessage(ProcessStatus.PREPARING.getDescription()) // 초기 상태 메시지
                        .build();

        // 4. 프로세스 저장
        newProcess = processRepository.save(newProcess);

        // 5. 예약에 생성된 프로세스 연관
        reservation.updateProcess(newProcess);
        reservationRepository.save(reservation);

        // 6. ProcessStatusDto 반환
        return createProcessStatusDto(newProcess, reservation.getIsDelivery());
    }

    @Transactional(readOnly = true)
    public ProcessStatusDto getProcessStatus(Long reservationId) {
        // 1. 예약 정보 조회
        Reservation reservation = getReservation(reservationId);

        // 2. 프로세스 정보 조회
        Process process = getProcess(reservation);

        // 3. ProcessStatusDto 생성 및 반환
        return createProcessStatusDto(process, reservation.getIsDelivery());
    }
}
