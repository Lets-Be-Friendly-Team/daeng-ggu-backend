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

    private Reservation getReservation(Long reservationId) {
        return reservationRepository
                .findById(reservationId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESERVATION_NOT_EXIST));
    }

    private Process getProcess(Reservation reservation) {
        Process process = reservation.getProcess();
        if (process == null) {
            throw new ApiException(ErrorCode.PROCESS_NOT_STARTED);
        }
        return process;
    }

    private ProcessStatusDto createProcessStatusDto(Process process, Boolean isDelivery) {
        return ProcessStatusDto.builder()
                .isDelivery(isDelivery != null ? isDelivery : false)
                .processNum(process.getProcessNum())
                .processStatus(process.getProcessStatus().name())
                .processMessage(process.getProcessMessage())
                .build();
    }

    private void updateProcessAndSave(Process process, ProcessStatus status, String description) {
        process.updateStatus(
                process.getProcessNum() + 1, // 무조건 +1
                status,
                description);
        processRepository.save(process);
    }

    private void updateStreamAndSave(Process process, String streamUrl, String channelARN) {
        process.updateStreamValue(streamUrl, channelARN);
        processRepository.save(process);
    }

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

    /**
     * 예약 ID를 기반으로 새로운 프로세스를 생성하는 메서드.
     *
     * @param reservationId 예약 ID
     * @return 생성된 프로세스 상태를 담은 DTO
     */
    @Transactional
    public ProcessStatusDto createProcess(Long reservationId) {

        Reservation reservation = getReservation(reservationId);

        // 이미 프로세스가 있는 경우 예외 처리
        if (reservation.getProcess() != null) {
            throw new ApiException(ErrorCode.PROCESS_ALREADY_EXISTS);
        }

        // 새로운 프로세스 생성
        Process newProcess =
                Process.builder()
                        .guardian(null) // TODO: 가디언 user 관리 기능 이후 추가 처리
                        .customerId(reservation.getPet().getCustomer().getCustomerId())
                        .processNum(1) // 초기 상태 번호
                        .processStatus(ProcessStatus.PREPARING) // 초기 상태
                        .processMessage(ProcessStatus.PREPARING.getDescription()) // 초기 상태 메시지
                        .build();

        newProcess = processRepository.save(newProcess);

        // 예약에 생성된 프로세스 연관
        reservation.updateProcess(newProcess);
        reservationRepository.save(reservation);

        return createProcessStatusDto(newProcess, reservation.getIsDelivery());
    }

    /**
     * 예약 ID를 기반으로 프로세스 상태를 조회하는 메서드.
     *
     * @param reservationId 예약 ID
     * @return 프로세스 상태를 담은 DTO
     */
    @Transactional(readOnly = true)
    public ProcessStatusDto getProcessStatus(Long reservationId) {

        Reservation reservation = getReservation(reservationId);
        Process process = getProcess(reservation);

        return createProcessStatusDto(process, reservation.getIsDelivery());
    }

    /**
     * 예약 ID를 기반으로 스트리밍 정보를 조회하는 메서드.
     *
     * @param reservationId 예약 ID
     * @return 스트리밍 정보를 담은 DTO
     */
    @Transactional(readOnly = true)
    public StreamingInfoDto getStreamingInfo(Long reservationId) {

        Reservation reservation = getReservation(reservationId);
        Process process = getProcess(reservation);

        return StreamingInfoDto.builder()
                .reservationId(reservationId)
                .streamUrl(process.getPlaybackUrl())
                .build();
    }

    // 디자이너 API

    /**
     * 예약 정보를 조회하여 디자이너를 위한 예약 정보를 반환하는 메서드.
     *
     * @param reservationId 예약 ID
     * @return 반려견 정보와 프로세스 상태 정보를 포함한 예약 정보 DTO
     * @throws ApiException 예약이 존재하지 않는 경우 예외 발생
     */
    @Transactional(readOnly = true)
    public ReservationInfoForDesignerDto getReservationInfo(Long reservationId) {

        Reservation reservation = getReservation(reservationId);

        Pet pet = reservation.getPet();
        Process process = reservation.getProcess();

        PetInfoDto petInfo = createPetInfoDto(pet);

        ProcessStatusDto processStatus =
                process != null
                        ? createProcessStatusDto(process, reservation.getIsDelivery())
                        : null;

        return ReservationInfoForDesignerDto.builder()
                .customerPhone(reservation.getPet().getCustomer().getPhone())
                .customerName(reservation.getPet().getCustomer().getCustomerName())
                .petInfo(petInfo)
                .status(processStatus)
                .build();
    }

    /**
     * 디자이너가 스트리밍을 시작하는 메서드.
     *
     * @param reservationId 예약 ID
     * @return 스트리밍 정보와 프로세스 상태를 포함한 DTO
     * @throws ApiException 예약이 존재하지 않거나 프로세스가 시작되지 않은 경우 예외 발생
     */
    @Transactional
    public StreamingDto designerStartStreaming(Long reservationId) {

        Reservation reservation = getReservation(reservationId);
        Process process = getProcess(reservation);

        updateProcessAndSave(
                process, ProcessStatus.GROOMING, ProcessStatus.GROOMING.getDescription());

        // 스트리밍 정보 생성
        String streamKey = "스트리밍 KEY"; // TODO: 스트리밍 키 생성 로직
        String streamUrl = "스트리밍 URL"; // TODO: 스트리밍 URL 생성 로직 -> channelARN
        updateStreamAndSave(process, streamUrl, streamKey);

        ProcessStatusDto processStatusDto =
                createProcessStatusDto(process, reservation.getIsDelivery());

        return StreamingDto.builder()
                .reservationId(reservationId)
                .channelARN(process.getChannelARN())
                .streamUrl(process.getPlaybackUrl())
                .statusDto(processStatusDto)
                .build();
    }

    /**
     * 디자이너가 스트리밍을 종료하는 메서드.
     *
     * @param reservationId 예약 ID
     * @return 업데이트된 프로세스 상태 정보
     * @throws ApiException 예약이 존재하지 않거나 프로세스가 시작되지 않은 경우 예외 발생
     */
    @Transactional
    public ProcessStatusDto designerEndStreaming(Long reservationId) {

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

        return createProcessStatusDto(process, reservation.getIsDelivery());
    }

    // 가디언 API

    /**
     * 가디언에게 보이는 예약 리스트를 조회하는 메서드
     *
     * @return List<ReservationInfoForGuardianDto>
     */
    @Transactional(readOnly = true)
    public List<ReservationInfoForGuardianDto> getUpcomingDeliveryReservations() {
        // 픽업이 필요한 예약 정보 조회
        List<Reservation> reservations =
                reservationRepository
                        .findByIsDeliveryTrueAndIsFinishedFalseOrderByReservationDateAscStartTimeAsc();

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

    /**
     * 예약 ID를 기반으로 가디언에게 예약 정보를 반환하는 메서드.
     *
     * @param reservationId 예약 ID
     * @return 가디언 위한 예약 정보 DTO
     */
    @Transactional(readOnly = true)
    public ReservationInfoForGuardianDto getGuardianReservationInfo(Long reservationId) {

        Reservation reservation = getReservation(reservationId);

        PetInfoDto petInfo = createPetInfoDto(reservation.getPet());

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

    /**
     * 예약 ID를 기반으로 미용실로 배송을 시작하는 메서드.
     *
     * @param reservationId 예약 ID
     * @return 스트리밍 정보와 상태를 포함한 DTO
     */
    @Transactional
    public StreamingDto startDeliveryToShop(Long reservationId) {

        Reservation reservation = getReservation(reservationId);
        Process process = getProcess(reservation);

        updateProcessAndSave(
                process,
                ProcessStatus.DELIVERY_TO_SHOP,
                ProcessStatus.DELIVERY_TO_SHOP.getDescription());

        // 스트리밍 정보 생성
        String streamKey = "스트리밍 KEY"; // TODO: 스트리밍 키 생성 로직
        String streamUrl = "스트리밍 URL"; // TODO: 스트리밍 URL 생성 로직
        updateStreamAndSave(process, streamUrl, streamKey);

        ProcessStatusDto processStatusDto =
                createProcessStatusDto(process, reservation.getIsDelivery());

        return StreamingDto.builder()
                .reservationId(reservationId)
                .streamUrl(process.getPlaybackUrl())
                .statusDto(processStatusDto)
                .build();
    }

    /**
     * 예약 ID를 기반으로 고객 집으로 배송을 시작하는 메서드.
     *
     * @param reservationId 예약 ID
     * @return 스트리밍 정보와 상태를 포함한 DTO
     */
    @Transactional
    public StreamingDto startDeliveryToHome(Long reservationId) {

        Reservation reservation = getReservation(reservationId);
        Process process = getProcess(reservation);

        updateProcessAndSave(
                process,
                ProcessStatus.DELIVERY_TO_HOME,
                ProcessStatus.DELIVERY_TO_HOME.getDescription());

        // 스트리밍 정보 생성
        String streamKey = "스트리밍 KEY"; // TODO: 스트리밍 키 생성 로직
        String streamUrl = "스트리밍 URL"; // TODO: 스트리밍 URL 생성 로직
        updateStreamAndSave(process, streamUrl, streamKey);

        ProcessStatusDto processStatusDto =
                createProcessStatusDto(process, reservation.getIsDelivery());

        return StreamingDto.builder()
                .reservationId(reservationId)
                .streamUrl(process.getPlaybackUrl())
                .statusDto(processStatusDto)
                .build();
    }

    /**
     * 예약 ID를 기반으로 고객 집에 도착한 프로세스 상태를 업데이트하는 메서드.
     *
     * @param reservationId 예약 ID
     * @return 업데이트된 프로세스 상태 DTO
     */
    @Transactional
    public ProcessStatusDto arriveAtHome(Long reservationId) {

        Reservation reservation = getReservation(reservationId);
        Process process = getProcess(reservation);

        updateProcessAndSave(
                process, ProcessStatus.COMPLETED, ProcessStatus.COMPLETED.getDescription());

        return createProcessStatusDto(process, reservation.getIsDelivery());
    }

    /**
     * 예약 ID를 기반으로 미용실에 도착한 프로세스 상태를 업데이트하는 메서드.
     *
     * @param reservationId 예약 ID
     * @return 업데이트된 프로세스 상태 DTO
     */
    @Transactional
    public ProcessStatusDto arriveAtShop(Long reservationId) {

        Reservation reservation = getReservation(reservationId);
        Process process = getProcess(reservation);

        updateProcessAndSave(
                process,
                ProcessStatus.WAITING_FOR_GROOMING,
                ProcessStatus.WAITING_FOR_GROOMING.getDescription());

        return createProcessStatusDto(process, reservation.getIsDelivery());
    }

    /**
     * 예약 ID를 통해 디자이너 정보를 조회
     *
     * @param reservationId 예약 ID
     * @return DesignerInfoDto
     * @throws ApiException RESERVATION_NOT_EXIST: 예약 정보가 존재하지 않을 경우
     */
    @Transactional(readOnly = true)
    public DesignerInfoDto getDesignerInfo(Long reservationId) {
        // 예약 정보 조회
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new ApiException(ErrorCode.RESERVATION_NOT_EXIST));

        // 디자이너 정보 가져오기
        return DesignerInfoDto.builder()
            .designerName(reservation.getDesigner().getDesignerName())
            .address(reservation.getDesigner().getAddress2() + " " + reservation.getDesigner().getDetailAddress())
            .officialName(reservation.getDesigner().getOfficialName())
            .introduction(reservation.getDesigner().getIntroduction())
            .phone(reservation.getDesigner().getPhone())
            .designerImgUrl(reservation.getDesigner().getDesignerImgUrl())
            .workExperience(reservation.getDesigner().getWorkExperience())
            .build();
    }
}
