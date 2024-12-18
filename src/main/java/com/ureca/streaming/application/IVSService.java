package com.ureca.streaming.application;

import static software.amazon.awssdk.services.ivs.model.ChannelLatencyMode.LOW;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ivs.IvsClient;
import software.amazon.awssdk.services.ivs.model.*;

// 송출자는 RTMP URL과 스트림 키로 방송을 시작하지만
// 시청자는 Playback URL만으로 방송을 시청 가능
@Service
@RequiredArgsConstructor
public class IVSService {

    private final IvsClient ivsClient;

    // 1. 채널 생성
    public String createChannel(String name) {
        CreateChannelRequest request =
                CreateChannelRequest.builder()
                        .name(name) // 예약 이름 기반으로 채널 생성
                        .latencyMode(LOW) // 저지연 모드 설정
                        .type(ChannelType.BASIC) // 채널 유형 설정 (BASIC/ADVANCED)
                        .build();

        CreateChannelResponse response = ivsClient.createChannel(request);
        return response.channel().arn();
    }

    // 2. 스트림 키 생성
    public String createStreamKey(String channelArn) {
        CreateStreamKeyRequest request =
                CreateStreamKeyRequest.builder().channelArn(channelArn).build();

        CreateStreamKeyResponse response = ivsClient.createStreamKey(request);
        return response.streamKey().value(); // 스트림 키 반환
    }

    // 3. 채널 정보 조회 (RTMP URL 포함)
    public String getRtmpEndpoint(String channelArn) {
        GetChannelRequest request = GetChannelRequest.builder().arn(channelArn).build();

        GetChannelResponse response = ivsClient.getChannel(request);
        return response.channel().ingestEndpoint(); // RTMP 엔드포인트 반환
    }

    // 4. 채널 삭제 (방송 종료 후 필요 시)

    public String getPlaybackUrl(String channelARN) {
        // 입력된 channelARN 검증
        if (channelARN == null || channelARN.isEmpty()) {
            throw new IllegalArgumentException("channelARN must not be null or empty.");
        }

        // AWS IVS 채널 요청
        GetChannelRequest request =
                GetChannelRequest.builder()
                        .arn(channelARN) // ARN 직접 사용
                        .build();

        // 채널 정보 가져오기
        GetChannelResponse response = ivsClient.getChannel(request);

        // Playback URL 반환
        return response.channel().playbackUrl();
    }

    public String getRTMPUrl(String channelArn) {

        // RTMP 엔드포인트 가져오기
        String ingestEndpoint = getRtmpEndpoint(channelArn);

        // 기존 스트림 키 조회
        String streamKey = getExistingStreamKey(channelArn);
        return "rtmp://" + ingestEndpoint + "/" + streamKey;
        // 송출 URL 생성
        //        return ingestEndpoint + "/" + streamKey;
    }

    public String getExistingStreamKey(String channelArn) {
        ListStreamKeysRequest request =
                ListStreamKeysRequest.builder().channelArn(channelArn).build();

        ListStreamKeysResponse response = ivsClient.listStreamKeys(request);

        if (response.streamKeys().isEmpty()) {
            throw new IllegalStateException("No stream keys found for channel: " + channelArn);
        }

        // 첫 번째 StreamKeySummary ARN 가져오기
        String streamKeyArn = response.streamKeys().get(0).arn();

        // GetStreamKey API 호출로 실제 스트림 키 값 얻기
        GetStreamKeyRequest getKeyRequest = GetStreamKeyRequest.builder().arn(streamKeyArn).build();

        GetStreamKeyResponse getKeyResponse = ivsClient.getStreamKey(getKeyRequest);

        return getKeyResponse.streamKey().value(); // 실제 스트림 키 값
    }

    public void deleteChannel(String channelArn) {
        DeleteChannelRequest request = DeleteChannelRequest.builder().arn(channelArn).build();

        ivsClient.deleteChannel(request); // AWS IVS 채널 삭제
        System.out.println("Channel deleted successfully: " + channelArn);
    }
}
