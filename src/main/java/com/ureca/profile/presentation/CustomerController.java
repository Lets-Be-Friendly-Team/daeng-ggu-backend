package com.ureca.profile.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/daengggu")
@Controller
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @GetMapping("/customer/profile")
    public String view() {
        logger.info("CD 설정 호출 성공!!!!");
        return "home";
    }
}
