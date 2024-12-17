package com.ureca.monitoring.domain;

import lombok.Getter;

@Getter
public enum ProcessStatus {
    PREPARING("서비스를 준비 중입니다"),
    DELIVERY_TO_SHOP("강아지가 미용실으로 이동 중입니다"),
    WAITING_FOR_GROOMING("강아지가 미용을 기다리고 있습니다"),
    GROOMING("미용이 진행 중입니다"),
    WAITING_FOR_DELIVERY("강아지가 집가기를 기다리고 있습니다"),
    DELIVERY_TO_HOME("강아지가 보호자님의 집으로 이동중입니다"),
    COMPLETED("미용이 완료되었어요");

    private final String description;

    ProcessStatus(String description) {
        this.description = description;
    }
}
