package com.becareful.becarefulserver.domain.matching.dto.request;

public enum ElderlyMatchingStatusFilter {
    매칭대기,
    매칭중,
    매칭완료;

    public boolean isMatchingProcessing() {
        return this.equals(ElderlyMatchingStatusFilter.매칭중);
    }

    public boolean isMatchingCompleted() {
        return this.equals(ElderlyMatchingStatusFilter.매칭완료);
    }
}
