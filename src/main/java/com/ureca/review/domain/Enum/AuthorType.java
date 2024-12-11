package com.ureca.review.domain.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AuthorType {
    DESIGNER,
    CUSTOMER,
    GUARDIAN;

    @JsonCreator
    public static AuthorType fromString(String key) {
        for (AuthorType value : AuthorType.values()) {
            if (value.name().equalsIgnoreCase(key)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid AuthorType: " + key);
    }

    @JsonValue
    public String toValue() {
        return name().toLowerCase(); // 값은 소문자로 변환하여 보냄
    }
}
