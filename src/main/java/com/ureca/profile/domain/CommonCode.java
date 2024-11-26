package com.ureca.profile.domain;

import jakarta.persistence.*;
import lombok.*;

// 공통 코드
@Entity
@Table(name = "commonCode")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class CommonCode {

    // 코드 아이디
    @Id private String codeId;

    // 코드명
    private String codeNm;

    // 코드 내용
    private String codeDesc;

    @Builder
    public CommonCode(String codeId, String codeNm, String codeDesc) {
        this.codeId = codeId;
        this.codeNm = codeNm;
        this.codeDesc = codeDesc;
    }
}
