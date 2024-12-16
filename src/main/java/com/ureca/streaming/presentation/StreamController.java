package com.ureca.streaming.presentation;

import com.ureca.streaming.application.IVSService;
import com.ureca.streaming.domain.ChannelInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ivs")
@RequiredArgsConstructor
public class StreamController {
    private final IVSService ivsService;

    @PostMapping("/create-channel")
    public ResponseEntity<ChannelInfo> createChannel(@RequestParam String channelName) {
        String channelArn = ivsService.createChannel(channelName);
        String streamKey = ivsService.createStreamKey(channelArn);
        String playbackUrl = ivsService.getPlaybackUrl(channelArn);
        // ChannelInfo 객체 생성
        ChannelInfo channelInfo = new ChannelInfo(channelArn, playbackUrl, streamKey);
        return ResponseEntity.ok(channelInfo);
    }
}
