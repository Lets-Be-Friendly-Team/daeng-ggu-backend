package com.ureca.estimate.application;

import com.ureca.common.application.S3Service;
import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.estimate.domain.Estimate;
import com.ureca.estimate.domain.EstimateImage;
import com.ureca.estimate.infrastructure.EstimateImageRepository;
import com.ureca.estimate.infrastructure.EstimateRepository;
import com.ureca.estimate.presentation.dto.EstimateDto;
import com.ureca.estimate.presentation.dto.EstimateDtoDetail;
import com.ureca.profile.domain.Pet;
import com.ureca.profile.infrastructure.DesignerRepository;
import com.ureca.profile.infrastructure.PetRepository;
import com.ureca.request.domain.Request;
import com.ureca.request.infrastructure.RequestRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class EstimateService {

    private final S3Service s3Service;
    private final EstimateRepository estimateRepository;
    private final EstimateImageRepository estimateImageRepository;
    private final RequestRepository requestRepository;
    private final DesignerRepository designerRepository;
    private final PetRepository petRepository;

    @Transactional
    public void makeEstimate(EstimateDto.Request request, List<MultipartFile> estimateImgList) {

        Estimate estimate =
                Estimate.builder()
                        .designer(
                                designerRepository.findByDesignerId(request.getDesignerId()).get())
                        .request(requestRepository.findById(request.getRequestId()).get())
                        .estimateDetail(request.getRequestDetail())
                        .desiredDate(request.getRequestDate())
                        .groomingFee(request.getGroomingFee())
                        .estimateStatus("ST1")
                        .build();

        List<EstimateImage> estimateImages = new ArrayList<>();
        for (MultipartFile estimateImg : estimateImgList) {
            String estimate_img_url =
                    s3Service.uploadFileImage(estimateImg, "estimate", "estimate");
            EstimateImage estimateImage =
                    EstimateImage.builder()
                            .estimateImgUrl(estimate_img_url)
                            .estimate(estimate)
                            .build();
            estimateImages.add(estimateImage);
        }
        estimate.toBuilder().estimateImages(estimateImages).build();

        estimateRepository.save(estimate);
    }

    public List<EstimateDto.Response> selectCustomerEstimate(Long customerId) {
        List<Pet> pets = petRepository.findByCustomerCustomerId(customerId);
        List<EstimateDto.Response> responseList = new ArrayList<>();
        for (Pet pet : pets) {
            Request request = requestRepository.findByPetAndRequest_status(pet, "ST1");
            if (request != null) {
                EstimateDto.Response response =
                        EstimateDto.Response.builder()
                                .petId(pet.getPetId())
                                .petName(pet.getPetName())
                                .petImageUrl(pet.getPetImgUrl())
                                .majorBreedCode(pet.getMajorBreedCode())
                                .desiredServiceCode(request.getDesiredServiceCode())
                                .lastGrommingDate(request.getLastGroomingDate())
                                .desiredDate1(request.getDesiredDate1())
                                .desiredDate2(request.getDesiredDate2())
                                .desiredDate3(request.getDesiredDate3())
                                .desiredRegion(request.getDesiredRegion())
                                .isVisitRequired(request.getIsDelivery())
                                .isMonitoringIncluded(request.getIsMonitoringIncluded())
                                .additionalRequest(request.getAdditionalRequest())
                                .createdAt(request.getCreatedAt())
                                .estimateList(
                                        estimateRepository.findAllByRequest(request).stream()
                                                .map(
                                                        estimate ->
                                                                EstimateDtoDetail.builder()
                                                                        .estimateId(
                                                                                estimate
                                                                                        .getEstimateId())
                                                                        .designerId(
                                                                                estimate.getDesigner()
                                                                                        .getDesignerId())
                                                                        .designerName(
                                                                                estimate.getDesigner()
                                                                                        .getDesignerName())
                                                                        .designerImageUrl(
                                                                                estimate.getDesigner()
                                                                                        .getDesignerImgUrl())
                                                                        .estimatePrice(
                                                                                estimate
                                                                                        .getGroomingFee())
                                                                        .petId(
                                                                                estimate.getRequest()
                                                                                        .getPet()
                                                                                        .getPetId())
                                                                        .petName(
                                                                                estimate.getRequest()
                                                                                        .getPet()
                                                                                        .getPetName())
                                                                        .createdAt(
                                                                                estimate
                                                                                        .getCreatedAt())
                                                                        .build())
                                                .collect(Collectors.toList()))
                                .build();
                responseList.add(response);
            }
        }

        return responseList;
    }

    public List<EstimateDto.Response> getPreviousEstimatesByDesigner(Long designerId) {
        List<Estimate> estimates = estimateRepository.findAllByDesignerDesignerId(designerId);

        List<EstimateDto.Response> estimateList =
                estimates.stream()
                        .map(
                                estimate ->
                                        EstimateDto.Response.builder()
                                                .petId(estimate.getRequest().getPet().getPetId())
                                                .petName(
                                                        estimate.getRequest().getPet().getPetName())
                                                .petImageUrl(
                                                        estimate.getRequest()
                                                                .getPet()
                                                                .getPetImgUrl())
                                                .desiredServiceCode(
                                                        estimate.getRequest()
                                                                .getDesiredServiceCode())
                                                .lastGrommingDate(
                                                        estimate.getRequest().getLastGroomingDate())
                                                .desiredDate1(
                                                        estimate.getRequest().getDesiredDate1())
                                                .desiredDate2(
                                                        estimate.getRequest().getDesiredDate2())
                                                .desiredDate3(
                                                        estimate.getRequest().getDesiredDate3())
                                                .desiredRegion(
                                                        estimate.getRequest().getDesiredRegion())
                                                .isVisitRequired(
                                                        estimate.getRequest().getIsDelivery())
                                                .isMonitoringIncluded(
                                                        estimate.getRequest()
                                                                .getIsMonitoringIncluded())
                                                .additionalRequest(
                                                        estimate.getRequest()
                                                                .getAdditionalRequest())
                                                .build())
                        .collect(Collectors.toList());

        return estimateList;
    }

    public EstimateDtoDetail getEstimateDetail(Long estimateId) {
        Estimate estimate =
                estimateRepository
                        .findById(estimateId)
                        .orElseThrow(() -> new ApiException(ErrorCode.ESTIMATE_NOT_EXIST));

        List<EstimateImage> estimateImages = estimateImageRepository.findAllByEstimate(estimate);
        List<String> estimateImgList = new ArrayList<>();
        for (EstimateImage estimateimage : estimateImages) {
            estimateImgList.add(estimateimage.getEstimateImgUrl());
        }

        return EstimateDtoDetail.builder()
                .estimateId(estimate.getEstimateId())
                .designerId(estimate.getDesigner().getDesignerId())
                .designerName(estimate.getDesigner().getDesignerName())
                .designerImageUrl(estimate.getDesigner().getDesignerImgUrl())
                .createdAt(estimate.getCreatedAt())
                .estimateDetail(estimate.getEstimateDetail())
                .customerId(estimate.getRequest().getCustomer().getCustomerId())
                .customerName(estimate.getRequest().getCustomer().getCustomerName())
                .phone(estimate.getRequest().getCustomer().getPhone())
                .address(estimate.getRequest().getCustomer().getAddress1())
                .groomingFee(estimate.getGroomingFee())
                .estimateImgList(estimateImgList)
                .build();
    }
}
