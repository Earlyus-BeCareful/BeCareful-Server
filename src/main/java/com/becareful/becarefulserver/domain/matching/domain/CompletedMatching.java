package com.becareful.becarefulserver.domain.matching.domain;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;

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
