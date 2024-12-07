package com.ureca.login.presentation.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class KakaoDTO {

    private long id;
    private String email;
    private String accessToken;
    private String refreshToken;
    private String role;
}
