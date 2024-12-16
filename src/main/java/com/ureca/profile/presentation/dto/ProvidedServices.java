package com.ureca.profile.presentation.dto;

import java.util.List;
import lombok.Data;

// 서비스별 견종 가격 및 시간
@Data
public class ProvidedServices {

    // 제공 서비스 코드
    private String serviceCode;

    // 서비스별 견종 가격 및 시간 목록
    private List<BreedPriceTime> breedPriceTimeList;
}
