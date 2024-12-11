package com.ureca.home.application;

import com.ureca.home.presentation.dto.HomeDesignerDetail;
import com.ureca.home.presentation.dto.HomeInfo;
import com.ureca.profile.infrastructure.DesignerRepository;
import com.ureca.profile.presentation.dto.Breed;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HomeService {

    private static final Logger logger = LoggerFactory.getLogger(HomeService.class);

    @Autowired private DesignerRepository designerRepository;

    /**
     * @title 보호자 홈 화면 - 디자이너 찾기
     * @description 전체 디자이너 목록, 인기 디자이너 목록, 프리미엄 제공 디자이너 목록
     * @param searchWord 검색어
     * @return HomeInfo 홈 데이터 정보
     */
    public HomeInfo getCustomerHome(String searchWord) {

        // 전체 디자이너 목록
        List<Object[]> resultAllDesigner = designerRepository.searchDesignersByKeyword(searchWord);
        List<HomeDesignerDetail> allDesignerList = new ArrayList<>();
        for (Object[] designer : resultAllDesigner) {
            Long designerId = (Long) designer[0];
            String designerName = (String) designer[1];
            String nickname = (String) designer[2];
            String designerImgUrl = (String) designer[3];
            Double reviewStarAvg = (Double) designer[4];
            Long bookmarkCnt = (Long) designer[5];
            String address1 = (String) designer[6];
            String address2 = (String) designer[7];
            String detailAddress = (String) designer[8];
            Double lng = (Double) designer[9];
            Double lat = (Double) designer[10];
            List<Breed> possibleBreedList = designerRepository.findDesignerMajorBreeds(designerId);
            HomeDesignerDetail homeDesignerDetail =
                    new HomeDesignerDetail(
                            designerId,
                            designerName,
                            nickname,
                            designerImgUrl,
                            reviewStarAvg,
                            bookmarkCnt,
                            address1,
                            address2,
                            detailAddress,
                            possibleBreedList,
                            lng,
                            lat);
            allDesignerList.add(homeDesignerDetail);
        }

        // 인기 디자이너 목록
        resultAllDesigner.stream()
                .sorted(
                        (d1, d2) -> {
                            Long bookmarkCnt1 = (Long) d1[5]; // bookmarkCnt가 6번째 위치에 있음
                            Long bookmarkCnt2 = (Long) d2[5]; // bookmarkCnt가 6번째 위치에 있음
                            return Long.compare(bookmarkCnt2, bookmarkCnt1); // 내림차순
                        })
                .collect(Collectors.toList());
        List<HomeDesignerDetail> sortedList =
                allDesignerList.stream()
                        .sorted(
                                Comparator.comparingLong(HomeDesignerDetail::getBookmarkCnt)
                                        .reversed()) // 내림차순 정렬
                        .collect(Collectors.toList());
        int limit = Math.min(sortedList.size(), 20);
        List<HomeDesignerDetail> popularList = sortedList.subList(0, limit); // 상위 20개 항목

        // 프리미엄 서비스 제공 디자이너 목록
        List<HomeDesignerDetail> premiumSpList = new ArrayList<>();
        List<HomeDesignerDetail> premiumFcList = new ArrayList<>();
        List<HomeDesignerDetail> premiumStList = new ArrayList<>();
        for (int i = 2; i < 5; i++) {
            String serviceCode = "S" + i;
            List<Object[]> resultPremiumDesigner =
                    designerRepository.findDesignersByProvidedServiceCode(serviceCode, searchWord);
            List<HomeDesignerDetail> premiumList = new ArrayList<>();
            for (Object[] designer : resultPremiumDesigner) {
                Long designerId = (Long) designer[0];
                String designerName = (String) designer[1];
                String nickname = (String) designer[2];
                String designerImgUrl = (String) designer[3];
                Double reviewStarAvg = (Double) designer[4];
                Long bookmarkCnt = (Long) designer[5];
                String address1 = (String) designer[6];
                String address2 = (String) designer[7];
                String detailAddress = (String) designer[8];
                Double lng = (Double) designer[9];
                Double lat = (Double) designer[10];
                List<Breed> possibleBreedList =
                        designerRepository.findDesignerMajorBreeds(designerId);
                HomeDesignerDetail homeDesignerDetail =
                        new HomeDesignerDetail(
                                designerId,
                                designerName,
                                nickname,
                                designerImgUrl,
                                reviewStarAvg,
                                bookmarkCnt,
                                address1,
                                address2,
                                detailAddress,
                                possibleBreedList,
                                lng,
                                lat);
                premiumList.add(homeDesignerDetail);
            }
            switch (serviceCode) {
                case "S2":
                    premiumSpList = premiumList;
                    break;
                case "S3":
                    premiumFcList = premiumList;
                    break;
                case "S4":
                    premiumStList = premiumList;
                    break;
            }
        }

        return HomeInfo.builder()
                .allDesignerList(allDesignerList)
                .popularList(popularList)
                .premiumSpList(premiumSpList)
                .premiumFcList(premiumFcList)
                .premiumStList(premiumStList)
                .build();
    } // getCustomerHome
}
