package com.becareful.becarefulserver.domain.matching.domain;

/**
 * - '지원검토'  - 지원 초기 상태
 * - '근무제안'  - 사회복지사가 요양보호사의 지원을 승인 / 매칭된 요양보호사에게 먼저 제안하는 경우 (채팅방 개설)
 * - '채용불발'  - 채팅방에서 채용이 이루어지지 않은 경우
 * - '채용완료'  - 채팅방에서 채용이 이루어진 경우
 */
public enum ApplicationStatus {
    지원검토,
    근무제안,
    채용불발,
    채용완료
}
