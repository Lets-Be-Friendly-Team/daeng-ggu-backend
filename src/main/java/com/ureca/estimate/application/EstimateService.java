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
import com.ureca.profile.infrastructure.BreedsRepository;
import com.ureca.profile.infrastructure.DesignerRepository;
import com.ureca.profile.infrastructure.PetRepository;
import com.ureca.profile.infrastructure.ServicesRepository;
import com.ureca.request.domain.Request;
import com.ureca.request.infrastructure.RequestRepository;
import com.ureca.review.domain.Enum.AuthorType;
import java.math.BigDecimal;
import java.time.Duration;
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

    @Transactional
    public void makeEstimate(EstimateDto.Request request, Long designerId) {
        BigDecimal totalFee =
                requestRepository
                        .findById(request.getRequestId())
                        .map(req -> req.getDeliveryFee().add(req.getMonitoringFee()))
                        .orElse(BigDecimal.ZERO);
        Request request1 =
                requestRepository
                        .findById(request.getRequestId())
                        .orElseThrow(() -> new ApiException(ErrorCode.REQUEST_NOT_EXIST));

        String lockKey = LOCK_KEY_PREFIX + request1.getRequestId();

        // 1. 락 시도 (유효 시간 5초)
        boolean lockAcquired = redisLockUtil.tryLock(lockKey, 5000);

        if (!lockAcquired) {
            throw new ApiException(ErrorCode.USER_CONFLICT_ERROR);
        }

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
            List<String> estimateImgList = request.getEstimateImgList();
            for (Integer i = 0; i < estimateImgList.size(); i++) {
                EstimateImage estimateImage =
                        EstimateImage.builder()
                                .estimateImgUrl(estimateImgList.get(i))
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
            redisLockUtil.unlock(lockKey);
        } else {
            redisLockUtil.unlock(lockKey);
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
                .startTime(estimate.getDesiredDate())
                .endTime(
                        estimate.getDesiredDate()
                                .plus(Duration.ofHours(estimate.getRequest().getServiceTime())))
                .takeTime(estimate.getRequest().getServiceTime())
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
