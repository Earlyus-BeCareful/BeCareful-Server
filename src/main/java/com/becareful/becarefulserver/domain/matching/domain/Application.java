package com.becareful.becarefulserver.domain.matching.domain;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.matching.domain.converter.MediationTypeSetConverter;
import com.becareful.becarefulserver.global.exception.exception.DomainException;
import com.becareful.becarefulserver.global.exception.exception.MatchingException;
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

    /**
     * Entity Method
     */
    public void propose() {
        validateProposable();
        this.applicationStatus = ApplicationStatus.근무제안;
    }

    public void postpone() {
        if (isPending) {
            throw new DomainException(APPLICATION_CANNOT_POSTPONE_ALREADY_PENDING);
        }
        this.isPending = true;
    }

    public void resume() {
        if (!isPending) {
            throw new DomainException(APPLICATION_CANNOT_RESUME_NOT_PENDING);
        }
        this.isPending = false;
    }

    private void validateProposable() {
        if (isPending) {
            throw new MatchingException(APPLICATION_CANNOT_PROPOSE_WHILE_PENDING);
        }

        if (applicationStatus.equals(ApplicationStatus.지원검토)) {
            return;
        }

        throw new DomainException(APPLICATION_CANNOT_PROPOSE_NOT_REVIEWING_APPLICATION_STATUS);
    }
}
