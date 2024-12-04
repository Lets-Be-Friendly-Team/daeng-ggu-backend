package com.ureca.request.application;

import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.profile.domain.Customer;
import com.ureca.profile.domain.Pet;
import com.ureca.profile.infrastructure.CommonCodeRepository;
import com.ureca.profile.infrastructure.CustomerRepository;
import com.ureca.profile.infrastructure.DesignerRepository;
import com.ureca.profile.infrastructure.PetRepository;
import com.ureca.request.domain.Request;
import com.ureca.request.infrastructure.RequestRepository;
import com.ureca.request.presentation.dto.RequestDto;
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
                            .build();
            responses.add(response);
        }
        return responses;
    }

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

    public void makeRequest(RequestDto.Request requestDto) {
        Pet pet =
                petRepository
                        .findById(requestDto.getPetId())
                        .orElseThrow(() -> new ApiException(ErrorCode.PET_NOT_EXIST));

        Customer customer = pet.getCustomer(); // Pet에 Customer 연관 관계가 있어야 합니다.
        String region = customer.getAddress1() + customer.getDetailAddress();
        Request request =
                Request.builder()
                        .pet(pet)
                        .customer(customer)
                        .desired_service_code(requestDto.getDesiredServiceCode())
                        .last_grooming_date(requestDto.getLastGroomingDate())
                        .desired_date1(requestDto.getDesiredDate1())
                        .desired_date2(requestDto.getDesiredDate2())
                        .desired_date3(requestDto.getDesiredDate3())
                        //                        .desired_region(requestDto.getDesiredRegion())//
                        // 주소 입력 받기
                        .desired_region(region) // 내 주소로 생성
                        .is_delivery(requestDto.getIsVisitRequired())
                        .is_monitoringIncluded(requestDto.getIsMonitoringIncluded())
                        .additional_request(requestDto.getAdditionalRequest())
                        .request_status("ST1") // 기본 상태
                        .build();

        requestRepository.save(request);
    }

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
                .desiredServiceCode(request.getDesired_service_code())
                .lastGroomingDate(request.getLast_grooming_date())
                .desiredDate1(request.getDesired_date1())
                .desiredDate2(request.getDesired_date2())
                .desiredDate3(request.getDesired_date3())
                .desiredRegion(request.getDesired_region())
                .isVisitRequired(request.getIs_delivery())
                .isMonitoringIncluded(request.getIs_monitoringIncluded())
                .additionalRequest(request.getAdditional_request())
                .build();
    }

    public List<RequestDto.Response> selectRequestBefore(Long customerId) {
        Customer customer = customerRepository.findByCustomerId(customerId).get();
        List<Request> requests = requestRepository.findAllByCustomer(customer);
        List<RequestDto.Response> responses = new ArrayList<>();
        for (Request request : requests) {
            RequestDto.Response response =
                    RequestDto.Response.builder()
                            .requestId(request.getRequest_id())
                            .petId(request.getPet().getPetId())
                            .petName(request.getPet().getPetName())
                            .petImageUrl(request.getPet().getPetImgUrl())
                            .majorBreed(
                                    commonCodeRepository.findCodeNmByCodeId(
                                            request.getPet().getMajorBreedCode()))
                            .desiredServiceCode(request.getDesired_service_code())
                            .isVisitRequired(request.getIs_delivery())
                            .createdAt(request.getCreatedAt())
                            .codeName(
                                    commonCodeRepository.findCodeNmByCodeId(
                                            request.getRequest_status()))
                            .build();
            if (response.getCodeName() != "ST1") {
                responses.add(response);
            }
        }
        return responses;
    }

    @Transactional
    public void deleteRequest(Long requestId) {
        Request request =
                requestRepository
                        .findById(requestId)
                        .orElseThrow(() -> new ApiException(ErrorCode.REQUEST_NOT_EXIST));
        requestRepository.delete(request);
    }
}