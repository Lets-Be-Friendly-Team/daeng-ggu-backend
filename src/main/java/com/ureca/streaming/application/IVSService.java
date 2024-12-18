package com.ureca.streaming.application;

import static software.amazon.awssdk.services.ivs.model.ChannelLatencyMode.LOW;

import com.amazonaws.SdkClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ivs.IvsClient;
import software.amazon.awssdk.services.ivs.model.*;

@Service
@RequiredArgsConstructor
public class IVSService {

    private final IvsClient ivsClient;

    // 1. 채널 생성
    public String createChannel(String name) {
        CreateChannelRequest request =
                CreateChannelRequest.builder()
                        .name(name)
                        .latencyMode(LOW)
                        .type(ChannelType.BASIC)
                        .build();

        CreateChannelResponse response = ivsClient.createChannel(request);
        return response.channel().arn();
    }

    // 2. 스트림 키 생성
    public String createStreamKey(String channelArn) {
        CreateStreamKeyRequest request =
                CreateStreamKeyRequest.builder().channelArn(channelArn).build();

        CreateStreamKeyResponse response = ivsClient.createStreamKey(request);
        return response.streamKey().value();
    }

    // 3. 채널 정보 조회 (RTMP URL 포함)
    public String getRtmpEndpoint(String channelArn) {
        GetChannelRequest request = GetChannelRequest.builder().arn(channelArn).build();

        GetChannelResponse response = ivsClient.getChannel(request);
        return response.channel().ingestEndpoint();
    }

    // Playback URL 조회
    public String getPlaybackUrl(String channelARN) {
        if (channelARN == null || channelARN.isEmpty()) {
            throw new IllegalArgumentException("channelARN must not be null or empty.");
        }

        GetChannelRequest request = GetChannelRequest.builder().arn(channelARN).build();

        GetChannelResponse response = ivsClient.getChannel(request);
        return response.channel().playbackUrl();
    }

    // RTMP URL 조회
    public String getRTMPUrl(String channelArn) {
        String ingestEndpoint = getRtmpEndpoint(channelArn);
        String streamKey = getExistingStreamKey(channelArn);
        return "rtmp://" + ingestEndpoint + "/" + streamKey;
    }

    // 기존 스트림 키 ARN 조회
    public String getExistingStreamKey(String channelArn) {
        ListStreamKeysRequest request =
                ListStreamKeysRequest.builder().channelArn(channelArn).build();

        ListStreamKeysResponse response = ivsClient.listStreamKeys(request);

        if (response.streamKeys().isEmpty()) {
            throw new IllegalStateException("No stream keys found for channel: " + channelArn);
        }

        return response.streamKeys().get(0).arn();
    }

    // 기존 스트림 키 값 조회
    public String getExistingStreamKey2(String channelArn) {
        ListStreamKeysRequest request =
                ListStreamKeysRequest.builder().channelArn(channelArn).build();

        ListStreamKeysResponse response = ivsClient.listStreamKeys(request);

        if (response.streamKeys().isEmpty()) {
            throw new IllegalStateException("No stream keys found for channel: " + channelArn);
        }

        String streamKeyArn = response.streamKeys().get(0).arn();
        GetStreamKeyRequest getKeyRequest = GetStreamKeyRequest.builder().arn(streamKeyArn).build();

        GetStreamKeyResponse getKeyResponse = ivsClient.getStreamKey(getKeyRequest);
        return getKeyResponse.streamKey().value();
    }

    // 스트림 키 삭제
    public void deleteStreamKey(String streamKeyArn) {
        try {
            ivsClient.deleteStreamKey(DeleteStreamKeyRequest.builder().arn(streamKeyArn).build());
            System.out.println("Successfully deleted StreamKey: " + streamKeyArn);
        } catch (ResourceNotFoundException e) {
            System.err.println("StreamKey not found: " + streamKeyArn);
        } catch (AccessDeniedException e) {
            System.err.println("Access denied to delete StreamKey: " + streamKeyArn);
        } catch (ValidationException e) {
            System.err.println("Invalid StreamKey ARN format: " + streamKeyArn);
        } catch (AwsServiceException | SdkClientException e) {
            System.err.println("Failed to delete StreamKey: " + e.getMessage());
        }
    }

    // 스트림 키 재설정
    public String resetStreamKey(String channelArn) {
        String streamKeyArn = null;

        try {
            streamKeyArn = getExistingStreamKey(channelArn);
            deleteStreamKey(streamKeyArn);
        } catch (ResourceNotFoundException e) {
            System.out.println("No existing StreamKey found. Creating a new one...");
        }

        return createStreamKey(channelArn);
    }

    // 채널 삭제
    public void deleteChannel(String channelArn) {
        DeleteChannelRequest request = DeleteChannelRequest.builder().arn(channelArn).build();

        ivsClient.deleteChannel(request);
        System.out.println("Channel deleted successfully: " + channelArn);
    }
}
