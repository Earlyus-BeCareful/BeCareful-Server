package com.becareful.becarefulserver.domain.caregiver.domain.vo;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CaregiverInfo {

    private boolean isHavingCar;

    private boolean isCompleteDementiaEducation;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "grade", column = @Column(name = "caregiver_certificate_grade")),
            @AttributeOverride(name = "certificateNumber", column = @Column(name = "caregiver_certificate_number"))
    })
    private Certificate caregiverCertificate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "grade", column = @Column(name = "social_worker_certificate_grade")),
            @AttributeOverride(name = "certificateNumber", column = @Column(name = "social_worker_certificate_number"))
    })
    private Certificate socialWorkerCertificate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "grade", column = @Column(name = "nursing_care_certificate_grade")),
            @AttributeOverride(name = "certificateNumber", column = @Column(name = "nursing_care_certificate_number"))
    })
    private Certificate nursingCareCertificate;
}
