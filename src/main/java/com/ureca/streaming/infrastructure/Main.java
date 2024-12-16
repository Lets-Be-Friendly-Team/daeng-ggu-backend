package com.ureca.streaming.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.streaming.domain.StreamStartResponse;

public class Main {
    public static void main(String[] args) throws Exception {
        String streamStartJson =
                """
        {
            "stream": {
                "channelArn": "arn:aws:ivs:region:account-id:channel/channel-id",
                "health": "HEALTHY",
                "playbackUrl": "https://abcdefg.live.video-url",
                "startTime": "2024-12-12T10:00:00Z",
                "state": "LIVE",
                "streamId": "stream-id-example"
            }
        }
        """;

        ObjectMapper objectMapper = new ObjectMapper();
        // JSON 데이터를 StreamStartResponse 객체로 역직렬화
        StreamStartResponse startResponse =
                objectMapper.readValue(streamStartJson, StreamStartResponse.class);

        System.out.println("Channel ARN: " + startResponse.getStream().getChannelArn());
        System.out.println("Playback URL: " + startResponse.getStream().getPlaybackUrl());
        System.out.println("State: " + startResponse.getStream().getState());
    }
}
