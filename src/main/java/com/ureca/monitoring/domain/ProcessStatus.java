package com.ureca.monitoring.domain;

import lombok.Getter;

@Getter
public enum ProcessStatus {

	PREPARING("시작 전 서비스 준비 중."),
	DELIVERY_TOSHOP("고객 집에서 미용소까지 배송 중."),
	WAITING_FOR_GROOMING("미용 시작 전 대기."),
	GROOMING("미용 진행 중."),
	WAITING_FOR_DELIVERY("미용 종료 후 배송 대기."),
	DELIVERY_TO_HOME("미용실에서 고객 집으로 배송 중."),
	COMPLETED("서비스 완료");

	private final String description;

	ProcessStatus(String description) {
		this.description = description;
	}
}