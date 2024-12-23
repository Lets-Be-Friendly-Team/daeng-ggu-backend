package com.ureca.estimate.application;

import com.ureca.alarm.application.AlarmService;
import com.ureca.alarm.presentation.dto.AlarmDto;
import com.ureca.common.application.S3Service;
import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import com.ureca.config.Redis.RedisLockUtil;
import com.ureca.estimate.domain.Estimate;
import com.ureca.estimate.domain.EstimateImage;
import com.ureca.estimate.infrastructure.EstimateImageRepository;
import com.ureca.estimate.infrastructure.EstimateRepository;
import com.ureca.estimate.presentation.dto.EstimateDto;
import com.ureca.estimate.presentation.dto.EstimateDtoDetail;
import com.ureca.profile.domain.Designer;
import com.ureca.profile.domain.Pet;
import com.ureca.profile.infrastructure.*;
import com.ureca.request.domain.Request;
import com.ureca.request.infrastructure.RequestRepository;
import com.ureca.review.domain.Enum.AuthorType;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EstimateService {

    private final S3Service s3Service;
    private final AlarmService alarmService;
    private final EstimateRepository estimateRepository;
    private final EstimateImageRepository estimateImageRepository;
    private final RequestRepository requestRepository;
    private final DesignerRepository designerRepository;
    private final BreedsRepository breedsRepository;
    private final ServicesRepository servicesRepository;
    private final PetRepository petRepository;
    private final RedisLockUtil redisLockUtil;

    private static final String LOCK_KEY_PREFIX = "estimate:cnt:";
    private final CommonCodeRepository commonCodeRepository;

    @Transactional
    public void makeEstimate(EstimateDto.Create estimateDto, Long designerId) {
        EstimateDto.Request request = estimateDto.getEstimateRequest();
        List<EstimateDto.Img> estimateImgList = estimateDto.getEstimateImgList();
        List<EstimateDto.TagId> estimateImgIdList = estimateDto.getEstimateImgIdList();
        BigDecimal totalFee =
                requestRepository
                        .findById(request.getRequestId())
                        .map(req -> req.getDeliveryFee().add(req.getMonitoringFee()))
                        .orElse(BigDecimal.ZERO);
        Request request1 =
                requestRepository
                        .findById(request.getRequestId())
                        .orElseThrow(() -> new ApiException(ErrorCode.REQUEST_NOT_EXIST));
        if (request1.getPet() == null) {
            throw new ApiException(ErrorCode.PET_NOT_EXIST);
        }

        String lockKey = LOCK_KEY_PREFIX + request1.getRequestId();

        //        // 1. 락 시도 (유효 시간 5초)
        //        boolean lockAcquired = redisLockUtil.tryLock(lockKey, 5000);
        //
        //        if (!lockAcquired) {
        //            throw new ApiException(ErrorCode.USER_CONFLICT_ERROR);
        //        }

        if (request1.getRequestCnt() < 10) {
            Estimate estimate =
                    Estimate.builder()
                            .designer(designerRepository.findByDesignerId(designerId).get())
                            .request(request1)
                            .estimateDetail(request.getRequestDetail())
                            .desiredDate(request.getRequestDate())
                            .groomingFee(request.getRequestPrice())
                            .estimatePayment(request.getRequestPrice().add(totalFee))
                            .estimateStatus("ST1")
                            .build();

            List<EstimateImage> estimateImages = new ArrayList<>();
            for (Integer i = 0; i < estimateImgList.size(); i++) {
                EstimateImage estimateImage =
                        EstimateImage.builder()
                                .estimateTagId(estimateImgIdList.get(i).getEstimateTagId())
                                .estimateImgUrl(estimateImgList.get(i).getEstimateImgUrl())
                                .estimate(estimate)
                                .build();
                estimateImageRepository.save(estimateImage);
                estimateImages.add(estimateImage);
            }
            estimate.toBuilder().estimateImages(estimateImages).build();

            estimateRepository.save(estimate);
            categoryAndAlarm(estimate);
            request1.setRequestCnt(request1.getRequestCnt() + 1);
            requestRepository.save(request1);
            //            redisLockUtil.unlock(lockKey);
        } else {
            //            redisLockUtil.unlock(lockKey);
            throw new ApiException(ErrorCode.REQUEST_FULL_ESTIMATE);
        }
    }

    private void categoryAndAlarm(Estimate estimate) {

        Designer designer = estimate.getDesigner();
        AlarmDto.Request alarmlist =
                AlarmDto.Request.builder()
                        .senderId(designer.getDesignerId())
                        .senderType(AuthorType.DESIGNER)
                        .receiverId(estimate.getRequest().getCustomer().getCustomerId())
                        .receiverType(AuthorType.CUSTOMER)
                        .objectId(estimate.getEstimateId())
                        .alarmType("A2")
                        .build();

        alarmService.sendNotification(alarmlist);
    }

    public List<EstimateDto.Response> selectCustomerEstimate(Long customerId) {
        List<Pet> pets = petRepository.findByCustomerCustomerId(customerId);
        List<EstimateDto.Response> responseList = new ArrayList<>();
        for (Pet pet : pets) {
            Request request = requestRepository.findByPetAndRequest_status(pet, "ST1");
            if (request != null) {
                EstimateDto.Response response =
                        EstimateDto.Response.builder()
                                .requestId(request.getRequestId())
                                .petId(pet.getPetId())
                                .petName(pet.getPetName())
                                .petImageUrl(pet.getPetImgUrl())
                                .birthDate(String.valueOf(pet.getBirthDate()))
                                .gender(pet.getGender())
                                .isNeutered(pet.getIsNeutered())
                                .weight(pet.getWeight())
                                .majorBreedCode(
                                        commonCodeRepository.findCodeNmByCodeId(
                                                pet.getMajorBreedCode()))
                                .majorBreed(
                                        commonCodeRepository.findCodeDescByCodeId(
                                                pet.getMajorBreedCode()))
                                .subBreed(
                                        commonCodeRepository.findCodeDescByCodeId(
                                                pet.getSubBreedCode()))
                                .desiredServiceCode(
                                        commonCodeRepository.findCodeDescByCodeId(
                                                request.getDesiredServiceCode()))
                                .lastGrommingDate(
                                        commonCodeRepository.findCodeDescByCodeId(
                                                request.getLastGroomingDate()))
                                .desiredDate1(request.getDesiredDate1())
                                .desiredDate2(request.getDesiredDate2())
                                .desiredDate3(request.getDesiredDate3())
                                .desiredRegion(request.getDesiredRegion())
                                .isVisitRequired(request.getIsDelivery())
                                .isMonitoringIncluded(request.getIsMonitoringIncluded())
                                .additionalRequest(request.getAdditionalRequest())
                                .address(
                                        request.getCustomer().getAddress1()
                                                + request.getCustomer().getDetailAddress())
                                .createdAt(request.getCreatedAt())
                                .estimateList(
                                        estimateRepository.findAllByRequest(request).stream()
                                                .filter(
                                                        estimate ->
                                                                estimate.getDesigner() != null
                                                                        && estimate.getDesigner()
                                                                                        .getDesignerName()
                                                                                != null) // 필터링 조건
                                                // 추가
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
                                                                        .designerAddress(
                                                                                estimate.getDesigner()
                                                                                                .getAddress1()
                                                                                        + estimate.getDesigner()
                                                                                                .getDetailAddress())
                                                                        .groomingFee(
                                                                                estimate
                                                                                        .getGroomingFee())
                                                                        .deliveryFee(
                                                                                estimate.getRequest()
                                                                                        .getDeliveryFee())
                                                                        .monitoringFee(
                                                                                estimate.getRequest()
                                                                                        .getMonitoringFee())
                                                                        .estimatePrice(
                                                                                estimate
                                                                                        .getEstimatePayment())
                                                                        .startTime(
                                                                                estimate
                                                                                        .getDesiredDate())
                                                                        .endTime(
                                                                                estimate.getDesiredDate()
                                                                                        .plus(
                                                                                                Duration
                                                                                                        .ofHours(
                                                                                                                request
                                                                                                                        .getServiceTime())))
                                                                        .takeTime(
                                                                                request
                                                                                        .getServiceTime())
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

    public LocalDate convertToLocalDate(String dateString, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(dateString, formatter);
    }

    public List<EstimateDto.Response> getPreviousEstimatesByDesigner(Long designerId) {
        List<Estimate> estimates = estimateRepository.findAllByDesignerDesignerId(designerId);

        List<EstimateDto.Response> estimateList =
                estimates.stream()
                        .filter(
                                estimate ->
                                        estimate.getRequest().getPet() != null
                                                && estimate.getRequest().getPet().getPetName()
                                                        != null) // 필터링 조건 추가
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
                                                        commonCodeRepository.findCodeDescByCodeId(
                                                                estimate.getRequest()
                                                                        .getDesiredServiceCode()))
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

        if (estimate.getDesigner() == null) {
            throw new ApiException(ErrorCode.DESIGNER_NOT_EXIST);
        } else if (estimate.getRequest().getPet().getPetName() == null) {
            throw new ApiException(ErrorCode.PET_NOT_EXIST);
        }

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
                .designerAddress(
                        estimate.getDesigner().getAddress1()
                                + estimate.getDesigner().getDetailAddress())
                .desiredServiceCode(
                        commonCodeRepository.findCodeDescByCodeId(
                                estimate.getRequest().getDesiredServiceCode()))
                .createdAt(estimate.getCreatedAt())
                .estimateDetail(estimate.getEstimateDetail())
                .startTime(estimate.getDesiredDate())
                .endTime(
                        estimate.getDesiredDate()
                                .plus(Duration.ofHours(estimate.getRequest().getServiceTime())))
                .takeTime(estimate.getRequest().getServiceTime())
                .petId(estimate.getRequest().getPet().getPetId())
                .petName(estimate.getRequest().getPet().getPetName())
                .customerId(estimate.getRequest().getCustomer().getCustomerId())
                .customerName(estimate.getRequest().getCustomer().getCustomerName())
                .phone(estimate.getRequest().getCustomer().getPhone())
                .address(estimate.getRequest().getCustomer().getAddress1())
                .groomingFee(estimate.getGroomingFee())
                .monitoringFee(estimate.getRequest().getMonitoringFee())
                .deliveryFee(estimate.getRequest().getDeliveryFee())
                .estimatePrice(estimate.getEstimatePayment())
                .estimateImgList(estimateImgList)
                .build();
    }
}
