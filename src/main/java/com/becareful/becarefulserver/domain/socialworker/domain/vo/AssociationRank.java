package com.becareful.becarefulserver.domain.socialworker.domain.vo;

public enum AssociationRank {
    CHAIRMAN(100),
    EXECUTIVE(50),
    MEMBER(1),
    NONE(0);

    public final int grade;

    AssociationRank(int grade) {
        this.grade = grade;
    }

    public boolean isLowerThan(AssociationRank other) {
        return grade < other.grade;
    }
}
