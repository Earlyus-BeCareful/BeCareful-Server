package com.becareful.becarefulserver.domain.caregiver.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import com.becareful.becarefulserver.domain.common.domain.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Career extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "career_id")
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private CareerType careerType;

    private String introduce;

    @JoinColumn(name = "caregiver_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Caregiver caregiver;

    @Builder(access = AccessLevel.PRIVATE)
    private Career(String title, CareerType careerType, String introduce, Caregiver caregiver) {
        this.title = title;
        this.careerType = careerType;
        this.introduce = introduce;
        this.caregiver = caregiver;
    }

    public static Career create(String title, CareerType careerType, String introduce, Caregiver caregiver) {
        return Career.builder()
                .title(title)
                .careerType(careerType)
                .introduce(introduce)
                .caregiver(caregiver)
                .build();
    }

    /***
     * entity method
     */

    public void updateCareer(String title, CareerType careerType, String introduce) {
        this.title = title;
        this.careerType = careerType;
        this.introduce = introduce;
    }

    public boolean hasCareer() {
        return this.careerType.equals(CareerType.경력);
    }
}
