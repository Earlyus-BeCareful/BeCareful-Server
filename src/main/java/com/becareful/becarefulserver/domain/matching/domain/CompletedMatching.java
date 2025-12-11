package com.becareful.becarefulserver.domain.matching.domain;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.chat.domain.Contract;
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

    @JoinColumn(name = "chat_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Contract contract;

    @JoinColumn(name = "recruitment_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Recruitment recruitment;

    public void updateNote(String note) {
        this.note = note;
    }

    public CompletedMatching(Caregiver caregiver, Contract contract, Recruitment recruitment) {
        this.caregiver = caregiver;
        this.contract = contract;
        this.recruitment = recruitment;
    }
}
