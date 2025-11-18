package com.becareful.becarefulserver.domain.matching.domain;

public enum RecruitmentStatus {
    모집중, // 매칭중
    조율중, // 근무 제안을 통해 채팅이 만들어진 상태
    모집완료, // 모집이 완료된 상태 (매칭 성공)
    공고마감; // 모집이 완료되지 않고 공고만 마감된 상태

    public boolean isCompleted() {
        return this == RecruitmentStatus.모집완료;
    }

    public boolean isClosed() {
        return this == RecruitmentStatus.공고마감;
    }

    public boolean isRecruiting() {
        return this == RecruitmentStatus.모집중;
    }
}
