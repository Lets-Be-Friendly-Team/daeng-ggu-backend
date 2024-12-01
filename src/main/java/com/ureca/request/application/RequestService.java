package com.ureca.request.application;

import com.ureca.profile.domain.Customer;
import com.ureca.profile.domain.Designer;
import com.ureca.profile.domain.Pet;
import com.ureca.profile.infrastructure.CustomerRepository;
import com.ureca.profile.infrastructure.DesignerRepository;
import com.ureca.profile.infrastructure.PetRepository;
import com.ureca.request.domain.Request;
import com.ureca.request.infrastructure.RequestRepository;
import com.ureca.request.presentation.dto.RequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final PetRepository petRepository;
    private final CustomerRepository customerRepository;
    private final DesignerRepository designerRepository;

    public void makeRequest(RequestDto.Request requestDto) {
        Pet pet = petRepository.findById(requestDto.getPet_id())
                .orElseThrow(() -> new IllegalArgumentException("반려견 정보를 찾을 수 없습니다."));

        Customer customer = pet.getCustomer(); // Pet에 Customer 연관 관계가 있어야 합니다.

        Request request = Request.builder()
                .pet(pet)
                .customer(customer)
                .desired_service_code(requestDto.getDesired_service_code())
                .last_grooming_date(requestDto.getLast_grooming_date())
                .desired_date1(requestDto.getDesired_date1())
                .desired_date2(requestDto.getDesired_date2())
                .desired_date3(requestDto.getDesired_date3())
                .desired_region(requestDto.getDesired_region())
                .is_delivery(requestDto.getIs_delivery())
                .is_monitoringIncluded(requestDto.getIs_monitoringIncluded())
                .additional_request(requestDto.getAdditional_request())
                .request_status("PENDING") // 기본 상태
                .build();

        requestRepository.save(request);
    }

    public RequestDto.Response selectRequest(Long request_id) {
        // 요청서를 ID로 조회
        Request request = requestRepository.findById(request_id)
                .orElseThrow(() -> new IllegalArgumentException("요청서를 찾을 수 없습니다."));

        // 조회된 데이터를 Response DTO로 변환
        return RequestDto.Response.builder()
                .pet_id(request.getPet().getPetId()) // Pet 엔티티의 ID
                .desired_service_code(request.getDesired_service_code())
                .last_grooming_date(request.getLast_grooming_date())
                .desired_date1(request.getDesired_date1())
                .desired_date2(request.getDesired_date2())
                .desired_date3(request.getDesired_date3())
                .desired_region(request.getDesired_region())
                .is_delivery(request.getIs_delivery())
                .is_monitoringIncluded(request.getIs_monitoringIncluded())
                .additional_request(request.getAdditional_request())
                .build();
    }

//    public List<RequestDto.Response> getRequestsForDesigner(Long designerId) {
//
//        Designer designer = designerRepository.findById(designerId).orElse(null);
//        // 디자이너 ID를 기준으로 요청서 조회
//        List<Request> requests = requestRepository.findByDesigner;
//
//        // 조회된 요청서를 DTO 리스트로 변환
//        return requests.stream()
//                .map(request -> RequestDto.DesignerResponse.builder()
//                        .request_id(request.getId())
//                        .pet_id(request.getPet().getId())
//                        .pet_name(request.getPet().getName())
//                        .pet_img_url(request.getPet().getImageUrl())
//                        .desired_service(request.getDesired_service_code())
//                        .is_delivery(request.getIs_delivery())
//                        .created_at(request.getCreated_at())
//                        .build())
//                .collect(Collectors.toList());
//    }
}
