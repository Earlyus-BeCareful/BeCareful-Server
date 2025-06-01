package com.becareful.becarefulserver.domain.caregiver.domain.vo;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Certificate {

    @Enumerated(EnumType.STRING)
    private CertificateGrade grade;

    private String certificateNumber;

    public enum CertificateGrade {
        FIRST("1급"),
        SECOND("2급");

        private final String value;

        CertificateGrade(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }
}
