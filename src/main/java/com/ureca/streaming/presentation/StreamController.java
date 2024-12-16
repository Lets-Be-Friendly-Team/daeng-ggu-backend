package com.ureca.streaming.presentation;

import com.ureca.streaming.application.IVSService;
import com.ureca.streaming.domain.ChannelInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * AWS IVS 채널을 생성하고 스트림 키와 Playback URL을 반환합니다.
 *
 * @return ChannelInfo 객체로 채널 ARN, 스트림 키, Playback URL을 반환합니다.
 */
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

        ChannelInfo channelInfo = new ChannelInfo(channelArn, streamKey, playbackUrl);
        return ResponseEntity.ok(channelInfo);
    }
}
