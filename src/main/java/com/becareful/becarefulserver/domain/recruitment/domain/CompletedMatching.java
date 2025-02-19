package com.becareful.becarefulserver.domain.recruitment.domain;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.converter.CareTypeSetConverter;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.EnumSet;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompletedMatching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "completed_matching_id")
    private Long id;

    private String note;

    @JoinColumn(name = "caregiver_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Caregiver caregiver;

    @JoinColumn(name = "contract_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Contract contract;

    public void updateNote(String note) {
        this.note = note;
    }

    public CompletedMatching(Caregiver caregiver, Contract contract) {
        this.caregiver = caregiver;
        this.contract = contract;
    }
}
