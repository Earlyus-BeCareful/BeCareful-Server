package com.becareful.becarefulserver.domain.common.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

import static com.becareful.becarefulserver.domain.common.domain.CareType.*;

public enum DetailCareType {
    스스로식사가능(식사보조, "스스로 식사 가능" ), 식사차려드리기(식사보조, "식사 차려드리기"), 요리필요(식사보조,"죽, 반찬 등 요리 필요"), 경관식보조(식사보조,"경관식 보조"),
    스스로거동가능(이동보조,"스스로 거동 가능"), 이동시부축도움(이동보조,"이동시 부축 도움"), 휠체어( 이동보조,"휠체어 이동 보조"), 거동불가(이동보조,"거동 불가"),
    스스로배변가능(배변보조,"스스로 배변 가능"),가끔실수(배변보조,"가끔 대소변 실수시 도움"), 기저귀케어(배변보조,"기저귀 케어 필요"),유치도뇨(배변보조,"유치도뇨/방광루/장루 관리"),
    가사보조(일상생활,"청소, 빨래 보조"),목욕보조(일상생활,"목욕 보조"), 운동보조(일상생활,"산책, 간단한 운동"), 정서지원(일상생활,"말벗 등 정서지원"), 인지자극(일상생활,"인지자극 활동"),
    병원동행(질병보조,"병원 동행"), 복약지도(질병보조,"복약지도"), 인지치료(질병보조,"인지치료"), 재활(질병보조,"재활프로그램");

    private final CareType careType;
    private final String displayName;
    DetailCareType(CareType careType, String displayName){
        this.careType = careType;
        this.displayName = displayName;
    }

    public CareType getCareType(){
        return careType;
    }

    @JsonValue //JSON 변환 시 사용될 값
    public String getDisplayName(){
        return displayName;
    }

    @JsonCreator //JSON -> Enum 변환 시 사용
    public static DetailCareType fromString(String value){
        return Arrays.stream(DetailCareType.values())
                .filter(item -> item.displayName.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown value: " + value));
    }
}
