package com.ureca.streaming.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * AWS IVS 채널 정보 DTO 클래스입니다.
 * 채널 ARN, 스트림 키, Playback URL을 포함합니다.
 */
@Data
@AllArgsConstructor
public class ChannelInfo {

    /**
     * AWS IVS 채널의 ARN (Amazon Resource Name).
     */

    private String channelArn;
    /**
     * AWS IVS 스트림 키. 방송 송출자가 사용하는 키입니다.
     */
    private String streamKey;
    /**
     * 시청자가 스트림을 재생할 수 있는 Playback URL.
     */
    private String playbackUrl;
}
