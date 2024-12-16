package com.ureca.streaming.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChannelInfo {
    private String channelArn;
    private String streamKey;
    private String rtmpEndpoint;
}
