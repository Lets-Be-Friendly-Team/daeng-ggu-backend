package com.ureca.streaming.presentation;

import com.ureca.streaming.application.IVSService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daengggu/delivery/stream")
public class StreamController {
    private final IVSService ivsService;


    /**
     * IVS 채널 생성
     * @param channelName 생성할 채널 이름
     * @return 채널 생성 결과 정보
     */

    @PostMapping("/start")
    public ResponseEntity<?> startStream(@RequestParam String channelName){


        return null;
    }

}
