package com.ureca.streaming.application;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ivs.IvsClient;
import software.amazon.awssdk.services.ivs.model.*;

import static software.amazon.awssdk.services.ivs.model.ChannelLatencyMode.LOW;
//송출자는 RTMP URL과 스트림 키로 방송을 시작하지만
//시청자는 Playback URL만으로 방송을 시청 가능
@Service
@RequiredArgsConstructor
public class IVSService {

    private final IvsClient ivsClient;

    // 1. 채널 생성
    public String createChannel(String name) {
        CreateChannelRequest request = CreateChannelRequest.builder()
                .name(name) // 예약 이름 기반으로 채널 생성
                .latencyMode(LOW) // 저지연 모드 설정
                .type(ChannelType.BASIC) // 채널 유형 설정 (BASIC/ADVANCED)
                .build();

        CreateChannelResponse response = ivsClient.createChannel(request);
        return response.channel().arn();
    }

    // 2. 스트림 키 생성
    public String createStreamKey(String channelArn) {
        CreateStreamKeyRequest request = CreateStreamKeyRequest.builder()
                .channelArn(channelArn)
                .build();

        CreateStreamKeyResponse response = ivsClient.createStreamKey(request);
        return response.streamKey().value(); // 스트림 키 반환
    }



    // 3. 채널 정보 조회 (RTMP URL 포함)
    public String getRtmpEndpoint(String channelArn) {
        GetChannelRequest request = GetChannelRequest.builder()
                .arn(channelArn)
                .build();

        GetChannelResponse response = ivsClient.getChannel(request);
        return response.channel().ingestEndpoint(); // RTMP 엔드포인트 반환
    }

    // 4. 채널 삭제 (방송 종료 후 필요 시)
    public void deleteChannel(String channelArn) {
        DeleteChannelRequest request = DeleteChannelRequest.builder()
                .arn(channelArn)
                .build();

        ivsClient.deleteChannel(request);
    }
    public String getPlaybackUrl(String channelId) {
        GetChannelRequest request = GetChannelRequest.builder()
                .arn("arn:aws:ivs:region:account-id:channel/" + channelId)
                .build();

        GetChannelResponse response = ivsClient.getChannel(request);
        return response.channel().playbackUrl(); // Playback URL 반환
    }

}



