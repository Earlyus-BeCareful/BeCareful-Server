package com.becareful.becarefulserver.domain.caregiver.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import com.becareful.becarefulserver.domain.work_location.domain.WorkLocation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkApplicationWorkLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "work_application_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private WorkApplication workApplication;

    @JoinColumn(name = "work_location_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private WorkLocation workLocation;

    private WorkApplicationWorkLocation(WorkApplication workApplication, WorkLocation workLocation) {
        this.workApplication = workApplication;
        this.workLocation = workLocation;
    }

    public static WorkApplicationWorkLocation of(WorkApplication workApplication, WorkLocation workLocation) {
        return new WorkApplicationWorkLocation(workApplication, workLocation);
    }
}
