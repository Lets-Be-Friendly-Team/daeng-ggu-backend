package com.ureca.streaming.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StreamErrorResponse {

    @JsonProperty("timestamp")
    private String timestamp; // ISO 8601 timestamp

    @JsonProperty("status")
    private int status;

    @JsonProperty("error")
    private String error;

    @JsonProperty("message")
    private String message;

    @JsonProperty("path")
    private String path;
}
