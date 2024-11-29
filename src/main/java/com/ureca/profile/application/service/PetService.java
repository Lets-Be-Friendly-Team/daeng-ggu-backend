package com.ureca.profile.application.service;

import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.profile.domain.Pet;
import com.ureca.profile.infrastructure.BookmarkRepository;
import com.ureca.profile.infrastructure.CommonCodeRepository;
import com.ureca.profile.infrastructure.CustomerRepository;
import com.ureca.profile.infrastructure.PetRepository;
import com.ureca.profile.presentation.dto.PetDetail;
import java.text.SimpleDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PetService {

    private static final Logger logger = LoggerFactory.getLogger(PetService.class);

    @Autowired private CustomerRepository customerRepository;
    @Autowired private PetRepository petRepository;
    @Autowired private CommonCodeRepository commonCodeRepository;
    @Autowired private BookmarkRepository bookmarkRepository;

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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String formattedDate = sdf.format(pet.getBirthDate());
            petDetail.setBirthDate(formattedDate);
        }

        return petDetail;
    } // getPetDetail
}
