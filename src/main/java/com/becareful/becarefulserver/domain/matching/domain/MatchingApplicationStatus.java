package com.becareful.becarefulserver.domain.matching.domain;

/**
 * - '미지원'    - 매칭 생성시 초기 상태
 * - '지원'     - 요양보호사가 원하는 공고에 지원 / 조율 지원한 경우
 * - '보류'     - 사회복지사가 요양보호사의 지원을 보류
 * - '합격'     - 채팅방에서 계약 완료
 */
public enum MatchingApplicationStatus {
    미지원,
    지원,
    보류,
    합격
}
