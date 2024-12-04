package com.ureca.home.presentation.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

// 보호자 홈 화면 - 디자이너 찾기
@Builder
@Getter
public class HomeInfo {

    // 전체 디자이너 목록
    private List<HomeDesignerDetail> allDesignerList;
    // 인기 디자이너 목록
    private List<HomeDesignerDetail> popularList;
    // 프리미엄 - 스파 제공 디자이너 목록
    private List<HomeDesignerDetail> premiumSpList;
    // 프리미엄 - 풀케어 제공 디자이너 목록
    private List<HomeDesignerDetail> premiumFcList;
    // 프리미엄 - 스트리밍 제공 디자이너 목록
    private List<HomeDesignerDetail> premiumStList;
}
