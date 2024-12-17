package com.ureca.streaming.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Getter, Setter, toString 자동 생성
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor
public class PlaybackChannelInfo {

    String playbackUrl;
}
