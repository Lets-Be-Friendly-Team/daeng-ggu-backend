package com.ureca.profile.application;

import com.ureca.common.application.S3Service;
import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.common.util.ValidationUtil;
import com.ureca.estimate.infrastructure.EstimateRepository;
import com.ureca.profile.domain.Customer;
import com.ureca.profile.domain.Pet;
import com.ureca.profile.infrastructure.CommonCodeRepository;
import com.ureca.profile.infrastructure.CustomerRepository;
import com.ureca.profile.infrastructure.PetRepository;
import com.ureca.profile.presentation.dto.PetDetail;
import com.ureca.profile.presentation.dto.PetUpdate;
import com.ureca.request.infrastructure.RequestRepository;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetService {

    private static final Logger logger = LoggerFactory.getLogger(PetService.class);

    @Autowired private CustomerRepository customerRepository;
    @Autowired private PetRepository petRepository;
    @Autowired private RequestRepository requestRepository;
    @Autowired private EstimateRepository estimateRepository;
    @Autowired private CommonCodeRepository commonCodeRepository;
    @Autowired private S3Service s3Service;

    /**
     * @title 반려견 - 프로필 상세
     * @description 반려견 상세 정보 조회
     * @param customerId 보호자 아이디
     * @param petId 반려견 아이디
     * @return CustomerDetail 보호자 프로필 상세 정보
     */
    public PetDetail getPetDetail(Long customerId, Long petId) {

        Pet pet = petRepository.findByCustomerCustomerIdAndPetId(customerId, petId);
        if (pet == null) {
            throw new ApiException(ErrorCode.DATA_NOT_EXIST);
        }
        String majorBreed =
                commonCodeRepository.findByCodeId(pet.getMajorBreedCode()).getCodeDesc();
        String subBreed = commonCodeRepository.findByCodeId(pet.getSubBreedCode()).getCodeDesc();
        PetDetail petDetail = new PetDetail();
        petDetail.setPetId(pet.getPetId());
        petDetail.setPetName(pet.getPetName());
        petDetail.setPetImgUrl(pet.getPetImgUrl());
        petDetail.setPetImgName(pet.getPetImgName());
        petDetail.setMajorBreedCode(pet.getMajorBreedCode());
        petDetail.setMajorBreed(majorBreed);
        petDetail.setSubBreedCode(pet.getSubBreedCode());
        petDetail.setSubBreed(subBreed);
        petDetail.setGender(pet.getGender());
        petDetail.setIsNeutered(pet.getIsNeutered());
        petDetail.setWeight(pet.getWeight());
        petDetail.setSpecialNotes(pet.getSpecialNotes());
        if (pet.getBirthDate() != null) {
            String formattedDate = ValidationUtil.dateToString(pet.getBirthDate());
            petDetail.setBirthDate(formattedDate);
        }

        return petDetail;
    } // getPetDetail

    /**
     * @title 반려견 - 프로필 등록/수정
     * @description 반려견 프로필 등록/수정
     * @param data 입력 정보
     * @return status 업데이트 성공 여부
     */
    @Transactional
    public void updatePetProfile(PetUpdate data) {
        Customer customer =
                customerRepository
                        .findById(data.getCustomerId())
                        .orElseThrow(() -> new ApiException(ErrorCode.CUSTOMER_NOT_EXIST));
        // 신규 등록
        if (data.getPetId() == null || data.getPetId() == 0) {
            Pet newPet =
                    Pet.builder()
                            .customer(customer)
                            .petName(data.getPetName())
                            .petImgUrl(data.getNewPetImgUrl())
                            .birthDate(ValidationUtil.stringToDate(data.getBirthDate()))
                            .gender(data.getGender())
                            .majorBreedCode(data.getMajorBreedCode())
                            .subBreedCode(data.getSubBreedCode())
                            .weight(data.getWeight())
                            .specialNotes(data.getSpecialNotes())
                            .isNeutered(data.getIsNeutered())
                            .createdAt(LocalDateTime.now())
                            .build();
            petRepository.save(newPet); // 등록

        } else {
            // 기존 정보 조회
            Pet pet =
                    petRepository.findByCustomerCustomerIdAndPetId(
                            data.getCustomerId(), data.getPetId());
            if (pet == null) {
                throw new ApiException(ErrorCode.DATA_NOT_EXIST);
            }
            String imageUrl = pet.getPetImgUrl();
            if (!data.getNewPetImgUrl().isEmpty()) { // 신규 이미지 업데이트
                imageUrl = data.getNewPetImgUrl();
            }
            Pet updatedPet =
                    pet.toBuilder()
                            .customer(customer)
                            .petName(data.getPetName())
                            .petImgUrl(data.getNewPetImgUrl())
                            .birthDate(ValidationUtil.stringToDate(data.getBirthDate()))
                            .gender(data.getGender())
                            .majorBreedCode(data.getMajorBreedCode())
                            .subBreedCode(data.getSubBreedCode())
                            .weight(data.getWeight())
                            .specialNotes(data.getSpecialNotes())
                            .isNeutered(data.getIsNeutered())
                            .updatedAt(LocalDateTime.now())
                            .build();
            petRepository.save(updatedPet); // 업데이트
        }
    } // updatePetProfile

    /**
     * @title 반려견 - 프로필 삭제
     * @description 반려견 프로필 삭제
     * @param customerId 보호자 아이디
     * @param petId 반려견 아이디
     */
    @Transactional
    public void deletePet(Long customerId, Long petId) {
        Customer customer =
                customerRepository
                        .findById(customerId)
                        .orElseThrow(() -> new ApiException(ErrorCode.DATA_NOT_EXIST));
        Pet pet = petRepository.findByCustomerCustomerIdAndPetId(customerId, petId);
        customer.getPets().remove(pet); // 고객 테이블에서는 삭제

        petRepository.updatePetNameToNull(petId); // 반려견면 null처리
    } // deletePet
}
