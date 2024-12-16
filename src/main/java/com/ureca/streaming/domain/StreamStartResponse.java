package com.ureca.streaming.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StreamStartResponse {

    @JsonProperty("stream")
    private StreamDetails stream;

    @Data
    public static class StreamDetails {
        @JsonProperty("channelArn")
        private String channelArn;

        @JsonProperty("health")
        private String health;

        @JsonProperty("playbackUrl")
        private String playbackUrl;

        @JsonProperty("startTime")
        private String startTime; // ISO 8601 timestamp

        @JsonProperty("state")
        private String state;

        @JsonProperty("streamId")
        private String streamId;
    }
}
