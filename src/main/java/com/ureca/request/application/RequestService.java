package com.ureca.request.application;

import com.ureca.alarm.application.AlarmService;
import com.ureca.alarm.infrastructure.AlarmRepository;
import com.ureca.alarm.presentation.dto.AlarmDto;
import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.profile.domain.Customer;
import com.ureca.profile.domain.Designer;
import com.ureca.profile.domain.Pet;
import com.ureca.profile.infrastructure.*;
import com.ureca.request.domain.Request;
import com.ureca.request.infrastructure.RequestRepository;
import com.ureca.request.presentation.dto.RequestDto;
import com.ureca.review.domain.Enum.AuthorType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final PetRepository petRepository;
    private final CustomerRepository customerRepository;
    private final DesignerRepository designerRepository;
    private final CommonCodeRepository commonCodeRepository;
    private final ServicesRepository servicesRepository;
    private final BreedsRepository breedsRepository;
    private final AlarmService alarmService;
    private final AlarmRepository alarmRepository;

    /**
     * 반려견의 프로필 목록을 조회하는 서비스
     *
     * @param customerId 고객 ID
     * @return 반려견 프로필 목록
     */
    public List<RequestDto.Response> selectPetProfile(Long customerId) {
        Customer customer = customerRepository.findById(customerId).get();
        List<Pet> pets = petRepository.findByCustomerCustomerId(customerId);
        List<RequestDto.Response> responses = new ArrayList<>();
        for (Pet pet : pets) {

            RequestDto.Response response =
                    RequestDto.Response.builder()
                            .petId(pet.getPetId())
                            .petName(pet.getPetName())
                            .petImageUrl(pet.getPetImgUrl())
                            .isPetRequested(
                                    requestRepository.existsByPetAndRequestStatus(pet, "ST1"))
                            .birthDate(String.valueOf(pet.getBirthDate()))
                            .gender(pet.getGender())
                            .isNeutered(pet.getIsNeutered())
                            .weight(pet.getWeight())
                            .majorBreedCode(String.valueOf(pet.getMajorBreedCode()))
                            .majorBreed(
                                    commonCodeRepository.findCodeNmByCodeId(
                                            pet.getMajorBreedCode()))
                            .subBreedCode(String.valueOf(pet.getSubBreedCode()))
                            .subBreed(
                                    commonCodeRepository.findCodeNmByCodeId(pet.getSubBreedCode()))
                            .specialNotes(pet.getSpecialNotes())
                            .isRequested(requestRepository.existsByPetAndRequestStatus(pet, "ST1"))
                            .customerId(customer.getCustomerId())
                            .customerName(customer.getCustomerName())
                            .phone(customer.getPhone())
                            .address(customer.getAddress1() + customer.getDetailAddress())
                            .build();
            responses.add(response);
        }
        return responses;
    }

    /**
     * 반려견의 상세 프로필을 조회하는 서비스
     *
     * @param customer_id 고객 ID
     * @param pet_id 반려견 ID
     * @return 반려견 상세 프로필
     */
    public RequestDto.Response selectPetProfileDetail(Long customer_id, Long pet_id) {

        Pet pet =
                petRepository
                        .findById(pet_id)
                        .orElseThrow(() -> new IllegalArgumentException("반려견 정보를 찾을 수 없습니다."));

        Customer customer = pet.getCustomer();

        RequestDto.Response response =
                RequestDto.Response.builder()
                        .petId(pet.getPetId())
                        .petName(pet.getPetName())
                        .petImageUrl(pet.getPetImgUrl())
                        .petImageName(pet.getPetImgName())
                        .birthDate(String.valueOf(pet.getBirthDate()))
                        .gender(pet.getGender())
                        .isNeutered(pet.getIsNeutered())
                        .weight(pet.getWeight())
                        .majorBreedCode(String.valueOf(pet.getMajorBreedCode()))
                        .majorBreed(
                                commonCodeRepository.findCodeNmByCodeId(pet.getMajorBreedCode()))
                        .subBreedCode(String.valueOf(pet.getSubBreedCode()))
                        .subBreed(commonCodeRepository.findCodeNmByCodeId(pet.getSubBreedCode()))
                        .specialNotes(pet.getSpecialNotes())
                        .isRequested(requestRepository.existsByPetAndRequestStatus(pet, "ST1"))
                        .customerId(customer.getCustomerId())
                        .customerName(customer.getCustomerName())
                        .phone(customer.getPhone())
                        .address(customer.getAddress1())
                        .build();

        return response;
    }

    /**
     * 견적 요청서를 생성하는 서비스
     *
     * @param requestDto 요청 정보 DTO
     */
    public void makeRequest(RequestDto.Request requestDto) {
        Pet pet =
                petRepository
                        .findById(requestDto.getPetId())
                        .orElseThrow(() -> new ApiException(ErrorCode.PET_NOT_EXIST));

        Customer customer = pet.getCustomer(); // Pet에 Customer 연관 관계가 있어야 합니다.
        String region = customer.getAddress1() + customer.getDetailAddress();
        BigDecimal deliveryFee = BigDecimal.ZERO;
        if (Boolean.TRUE.equals(requestDto.getIsVisitRequired())) {
            // 견종의 종류에 따라 가격을 설정
            switch (pet.getMajorBreedCode()) {
                case "P1":
                    deliveryFee = BigDecimal.valueOf(20000);
                    break;
                case "P2":
                    deliveryFee = BigDecimal.valueOf(30000);
                    break;
                case "P3":
                    deliveryFee = BigDecimal.valueOf(40000);
                    break;
                case "P4":
                    deliveryFee = BigDecimal.valueOf(50000);
                    break;
                default:
                    throw new ApiException(ErrorCode.INVALID_BREED);
            }
        }

        // isMonitoringIncluded에 따라 monitoringFee 계산
        BigDecimal monitoringFee =
                Boolean.TRUE.equals(requestDto.getIsMonitoringIncluded())
                        ? BigDecimal.valueOf(50000)
                        : BigDecimal.ZERO;
        Request request =
                Request.builder()
                        .pet(pet)
                        .customer(customer)
                        .desiredServiceCode(requestDto.getDesiredServiceCode())
                        .lastGroomingDate(requestDto.getLastGroomingDate())
                        .desiredDate1(requestDto.getDesiredDate1())
                        .desiredDate2(requestDto.getDesiredDate2())
                        .desiredDate3(requestDto.getDesiredDate3())
                        //                        .desired_region(requestDto.getDesiredRegion())//
                        // 주소 입력 받기
                        .desiredRegion(requestDto.getDesiredRegion()) // 내 주소로 생성
                        .isDelivery(requestDto.getIsVisitRequired())
                        .isMonitoringIncluded(requestDto.getIsMonitoringIncluded())
                        .additionalRequest(requestDto.getAdditionalRequest())
                        .deliveryFee(deliveryFee)
                        .monitoringFee(monitoringFee)
                        .requestStatus("ST1") // 기본 상태
                        .build();

        requestRepository.save(request);
        categoryAndAlarm(request);
    }

    /**
     * 요청서를 생성한 후 관련된 디자이너들에게 알림을 보내는 서비스
     *
     * @param request 요청 정보
     */
    private void categoryAndAlarm(Request request) {
        List<Designer> designers =
                servicesRepository.findDesignerByProvidedServicesCode(
                        request.getDesiredServiceCode());
        designers =
                breedsRepository.findDesignerByPossibleMajorBreedCode(
                        request.getPet().getMajorBreedCode(), designers);
        List<AlarmDto.Request> alarmList = new ArrayList<>();
        for (Designer designer : designers) {
            AlarmDto.Request alarmlist =
                    AlarmDto.Request.builder()
                            .senderId(request.getCustomer().getCustomerId())
                            .senderType(AuthorType.CUSTOMER)
                            .receiverId(designer.getDesignerId())
                            .receiverType(AuthorType.DESIGNER)
                            .objectId(request.getRequestId())
                            .alarmType("A1")
                            .build();

            alarmList.add(alarmlist);
        }
        alarmService.sendNotificationsToUsers(alarmList);
    }

    /**
     * 요청서를 ID로 조회하는 서비스
     *
     * @param request_id 요청서 ID
     * @return 요청서 상세 정보
     */
    public RequestDto.Response selectRequest(Long request_id) {
        // 요청서를 ID로 조회
        Request request =
                requestRepository
                        .findById(request_id)
                        .orElseThrow(() -> new ApiException(ErrorCode.REQUEST_NOT_EXIST));

        Pet pet = request.getPet();
        Customer customer = pet.getCustomer();

        // 조회된 데이터를 Response DTO로 변환
        return RequestDto.Response.builder()
                .petId(pet.getPetId())
                .petName(pet.getPetName())
                .petImageUrl(pet.getPetImgUrl())
                .petImageName(pet.getPetImgName())
                .birthDate(String.valueOf(pet.getBirthDate()))
                .gender(pet.getGender())
                .isNeutered(pet.getIsNeutered())
                .weight(pet.getWeight())
                .majorBreedCode(String.valueOf(pet.getMajorBreedCode()))
                .majorBreed(commonCodeRepository.findCodeNmByCodeId(pet.getMajorBreedCode()))
                .subBreedCode(String.valueOf(pet.getSubBreedCode()))
                .subBreed(commonCodeRepository.findCodeNmByCodeId(pet.getSubBreedCode()))
                .specialNotes(pet.getSpecialNotes())
                .isRequested(requestRepository.existsByPetAndRequestStatus(pet, "ST1"))
                .customerId(customer.getCustomerId())
                .customerName(customer.getCustomerName())
                .phone(customer.getPhone())
                .address(customer.getAddress1())
                .desiredServiceCode(request.getDesiredServiceCode())
                .lastGroomingDate(request.getLastGroomingDate())
                .desiredDate1(request.getDesiredDate1())
                .desiredDate2(request.getDesiredDate2())
                .desiredDate3(request.getDesiredDate3())
                .desiredRegion(request.getDesiredRegion())
                .isVisitRequired(request.getIsDelivery())
                .isMonitoringIncluded(request.getIsMonitoringIncluded())
                .additionalRequest(request.getAdditionalRequest())
                .deliveryFee(request.getDeliveryFee())
                .monitoringFee(request.getMonitoringFee())
                .build();
    }

    /**
     * 고객 ID로 이전의 요청서를 조회하는 서비스
     *
     * @param customerId 고객 ID
     * @return 이전 요청서 목록
     */
    public List<RequestDto.Response> selectRequestBefore(Long customerId) {
        Customer customer = customerRepository.findByCustomerId(customerId).get();
        List<Request> requests = requestRepository.findAllByCustomer(customer);
        List<RequestDto.Response> responses = new ArrayList<>();
        for (Request request : requests) {
            RequestDto.Response response =
                    RequestDto.Response.builder()
                            .requestId(request.getRequestId())
                            .petId(request.getPet().getPetId())
                            .petName(request.getPet().getPetName())
                            .petImageUrl(request.getPet().getPetImgUrl())
                            .majorBreed(
                                    commonCodeRepository.findCodeNmByCodeId(
                                            request.getPet().getMajorBreedCode()))
                            .desiredServiceCode(request.getDesiredServiceCode())
                            .isVisitRequired(request.getIsDelivery())
                            .createdAt(request.getCreatedAt())
                            .codeName(
                                    commonCodeRepository.findCodeNmByCodeId(
                                            request.getRequestStatus()))
                            .build();
            if (response.getCodeName() != "ST1") {
                responses.add(response);
            }
        }
        return responses;
    }

    /**
     * 요청서를 삭제하는 서비스 요청 상태가 'ST1' 또는 'ST3'일 경우에만 요청서를 삭제할 수 있습니다. 그 외의 상태에서는 삭제할 수 없으며 예외를 발생시킵니다.
     *
     * @param requestId 삭제할 요청서의 ID
     */
    @Transactional
    public void deleteRequest(Long requestId) {
        Request request =
                requestRepository
                        .findById(requestId)
                        .orElseThrow(() -> new ApiException(ErrorCode.REQUEST_NOT_EXIST));
        if (request.getRequestStatus() == "ST1" || request.getRequestStatus() == "ST3") {
            requestRepository.delete(request);
        } else {
            throw new ApiException(ErrorCode.RESERVE_EXIST_ERROR);
        }
    }

    /**
     * 디자이너에게 할당된 요청서 목록을 조회하는 서비스 디자이너가 수신한 알림에 따라 요청서를 찾아서 반환합니다.
     *
     * @param designerId 디자이너 ID
     * @return 디자이너에게 할당된 요청서 목록
     */
    @Transactional
    public List<RequestDto.Response> selectDesignerRequest(Long designerId) {
        List<Long> requestIdList =
                alarmRepository.findObjectIdByReceiverIdAndAlarmType(designerId, "A1");
        List<RequestDto.Response> reqList = new ArrayList<>();
        for (Long requestId : requestIdList) {
            RequestDto.Response response =
                    RequestDto.Response.builder()
                            .requestId(requestId)
                            .petId(requestRepository.findById(requestId).get().getPet().getPetId())
                            .petName(
                                    requestRepository
                                            .findById(requestId)
                                            .get()
                                            .getPet()
                                            .getPetName())
                            .petImageUrl(
                                    requestRepository
                                            .findById(requestId)
                                            .get()
                                            .getPet()
                                            .getPetImgUrl())
                            .majorBreedCode(
                                    requestRepository
                                            .findById(requestId)
                                            .get()
                                            .getPet()
                                            .getMajorBreedCode())
                            .desiredServiceCode(
                                    requestRepository
                                            .findById(requestId)
                                            .get()
                                            .getDesiredServiceCode())
                            .isVisitRequired(
                                    requestRepository.findById(requestId).get().getIsDelivery())
                            .createdAt(requestRepository.findById(requestId).get().getCreatedAt())
                            .build();
            reqList.add(response);
        }
        return reqList;
    }
}
