package com.becareful.becarefulserver.domain.caregiver.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import com.becareful.becarefulserver.domain.common.domain.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CareerDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "career_detail_id")
    private Long id;

    private String workInstitution;

    private Integer workYear;

    @JoinColumn(name = "career_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Career career;

    @Builder(access = AccessLevel.PRIVATE)
    private CareerDetail(String workInstitution, Integer workYear, Career career) {
        this.workInstitution = workInstitution;
        this.workYear = workYear;
        this.career = career;
    }

    public static CareerDetail create(String workInstitution, Integer workYear, Career career) {
        return CareerDetail.builder()
                .workInstitution(workInstitution)
                .workYear(workYear)
                .career(career)
                .build();
    }
}
