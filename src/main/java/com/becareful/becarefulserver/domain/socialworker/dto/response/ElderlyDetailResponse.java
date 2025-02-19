package com.becareful.becarefulserver.domain.socialworker.dto.response;

import java.util.List;

public record ElderlyDetailResponse(
        ElderlyInfo elderlyInfo,

        CareInfo careInfo,
        CareGiverInfoList careGiverInfoList,
        RecruimentInfoList recruimentInfoList
) {

    public record ElderlyHeader(){
        //이미지 주소
        //매칭중?
        //성함
    }
    public record ElderlyInfo(){
        //생년
        //나이
        //주소
        //상세 주소
        //동거
        //애완

    }

    public record CareInfo(
            //건강 상태
            //요양등급
            //케어필요항목 - 디테일이랑 타입
            String detail // 예제 필드 추가
    ) {}

    public record CareGiverInfoList(
            List<CareGiverInfo> caregivers
    ) {}

    public record CareGiverInfo(//매칭 완료에서 가져오기
            //보호사 사진
            //이름
            //요일
            //시간
            //케어항목
    ) {}

    public record RecruitmentInfo(
            //
    ){}

    public record RecruimentInfoList(){}
}
