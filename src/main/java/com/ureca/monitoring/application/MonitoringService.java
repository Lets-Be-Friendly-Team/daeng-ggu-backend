package com.ureca.monitoring.application;

import com.ureca.common.exception.ApiException;
import com.ureca.monitoring.domain.Process;
import com.ureca.monitoring.presentaion.dto.ReservationInfoForDesignerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ureca.reservation.domain.Reservation;
import com.ureca.reservation.infrastructure.ReservationRepository;
import com.ureca.profile.domain.Pet;
import com.ureca.monitoring.presentaion.dto.*;
import com.ureca.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class MonitoringService {

    private final ReservationRepository reservationRepository;

    /**
     * 예약 정보를 조회하여 ReservationInfoForDesignerDto로 변환하는 메서드
     *
     * @param reservationId 예약 ID
     * @return ReservationInfoForDesignerDto
     */
    @Transactional(readOnly = true)
    public ReservationInfoForDesignerDto getReservationInfo(Long reservationId) {
        // 예약 정보 조회
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new ApiException(ErrorCode.RESERVATION_NOT_EXIST));

        // 반려견 정보 가져오기
        Pet pet = reservation.getPet();

        // Process 데이터 가져오기
        Process process = reservation.getProcess();

        // PetInfoDto 생성
        PetInfoDto petInfo = PetInfoDto.builder()
            .petName(pet.getPetName())
            .birthDate(pet.getBirthDate().toString())
            .gender(pet.getGender())
            .weight(pet.getWeight())
            .specialNotes(pet.getSpecialNotes())
            .isNeutered(pet.getIsNeutered() != null && pet.getIsNeutered().equalsIgnoreCase("true"))
            .majorBreed(pet.getMajorBreedCode())
            .subBreed(pet.getSubBreedCode())
            .build();

        // ProcessStatusDto 생성
        ProcessStatusDto processStatus = process != null
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
            .status(processStatus)   // 진행 상태
            .build();
    }
}
