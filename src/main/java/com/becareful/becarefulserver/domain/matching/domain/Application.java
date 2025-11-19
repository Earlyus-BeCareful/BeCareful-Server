package com.becareful.becarefulserver.domain.matching.domain;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.matching.domain.converter.MediationTypeSetConverter;
import jakarta.persistence.*;
import java.util.EnumSet;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_application_resume_recruitment",
                    columnNames = {"work_application_id", "recruitment_id"})
        })
@Entity
public class Application extends BaseEntity {

    @Id
    @Column(name = "application_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus applicationStatus;

    private Boolean isPending;

    @Convert(converter = MediationTypeSetConverter.class)
    private EnumSet<MediationType> mediationTypes;

    private String mediationDescription;

    @JoinColumn(name = "recruitment_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Recruitment recruitment;

    @JoinColumn(name = "work_application_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private WorkApplication workApplication;

    @Builder(access = AccessLevel.PRIVATE)
    private Application(
            EnumSet<MediationType> mediationTypes,
            String mediationDescription,
            Recruitment recruitment,
            WorkApplication workApplication) {
        this.applicationStatus = ApplicationStatus.지원검토;
        this.isPending = false;
        this.mediationTypes = mediationTypes;
        this.mediationDescription = mediationDescription;
        this.recruitment = recruitment;
        this.workApplication = workApplication;
    }

    public static Application general(Recruitment recruitment, WorkApplication workApplication) {
        return Application.builder()
                .recruitment(recruitment)
                .workApplication(workApplication)
                .build();
    }

    public static Application mediated(
            Recruitment recruitment,
            WorkApplication workApplication,
            List<MediationType> mediationTypes,
            String mediationDescription) {
        return Application.builder()
                .mediationTypes(EnumSet.copyOf(mediationTypes))
                .mediationDescription(mediationDescription)
                .recruitment(recruitment)
                .workApplication(workApplication)
                .build();
    }
}
