package com.becareful.becarefulserver.domain.nursing_institution.domain.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.Arrays;

public enum FacilityType {
    방문요양("방문 요양"),
    방문목욕("방문 목욕"),
    방문간호("방문 간호"),
    주야간보호("주야간 보호"),
    단기보호("단기 보호"),
    복지용구("복지 용구");

    private final String displayName;

    FacilityType(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue // JSON 변환 시 사용될 값
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator // JSON -> Enum 변환 시 사용
    public static FacilityType fromString(String value) {
        return Arrays.stream(FacilityType.values())
                .filter(facilityType -> facilityType.displayName.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown value: " + value));
    }

    @Enumerated(EnumType.STRING) // DB에 문자열로 저장
    public String toString() {
        return this.name(); // enum 이름을 사용
    }
}
