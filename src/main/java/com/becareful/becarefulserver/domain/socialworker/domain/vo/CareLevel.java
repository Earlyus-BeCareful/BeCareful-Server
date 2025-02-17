package com.becareful.becarefulserver.domain.socialworker.domain.vo;

import com.becareful.becarefulserver.domain.common.domain.DetailCareType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.Arrays;

public enum CareLevel {
    일등급("1등급"),이등급("2등급"),삼등급("3등급"),사등급("4등급"),오등급("5등급"),인지지원등급("인지지원등급"),등급없음("등급없음");

    private final String displayName;
    CareLevel(String displayName){
        this.displayName = displayName;
    }

    @JsonValue //JSON 변환 시 사용될 값
    public String getDisplayName(){
        return displayName;
    }

    @JsonCreator //JSON -> Enum 변환 시 사용
    public static CareLevel fromString(String value){
        return Arrays.stream(CareLevel.values())
                .filter(careLevel -> careLevel.displayName.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown value: " + value));
    }

    @Enumerated(EnumType.STRING) // DB에 문자열로 저장
    public String toString() {
        return this.name(); // enum 이름을 사용
    }

}
