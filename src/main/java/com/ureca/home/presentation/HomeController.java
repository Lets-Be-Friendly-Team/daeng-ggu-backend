package com.ureca.home.presentation;

import com.ureca.common.response.ResponseDto;
import com.ureca.common.response.ResponseUtil;
import com.ureca.home.presentation.dto.HomeInfo;
import com.ureca.home.service.HomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/daengggu")
@RestController
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired private HomeService homeService;

    /**
     * @title 보호자 홈 화면 - 디자이너 찾기
     * @param searchWord 검색어
     * @description /home
     */
    @GetMapping("/home")
    public ResponseDto<HomeInfo> customerHome(@RequestParam(defaultValue = "") String searchWord) {
        // service - 홈 조회
        return ResponseUtil.SUCCESS("처리가 완료되었습니다.", homeService.getCustomerHome(searchWord));
    } // customerHome
}
