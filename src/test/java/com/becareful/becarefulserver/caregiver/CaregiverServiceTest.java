package com.becareful.becarefulserver.caregiver;

import com.becareful.becarefulserver.common.IntegrationTest;
import com.becareful.becarefulserver.common.WithCaregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.caregiver.dto.request.CareerCreateOrUpdateRequest;
import com.becareful.becarefulserver.domain.caregiver.dto.request.CareerDetailUpdateRequest;
import com.becareful.becarefulserver.domain.caregiver.dto.request.WorkApplicationCreateOrUpdateRequest;
import com.becareful.becarefulserver.domain.caregiver.repository.CaregiverRepository;
import com.becareful.becarefulserver.domain.caregiver.repository.WorkApplicationRepository;
import com.becareful.becarefulserver.domain.caregiver.service.CareerService;
import com.becareful.becarefulserver.domain.caregiver.service.CaregiverService;
import com.becareful.becarefulserver.domain.caregiver.service.WorkApplicationService;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.common.domain.DetailCareType;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.common.domain.vo.Location;
import com.becareful.becarefulserver.domain.matching.domain.Application;
import com.becareful.becarefulserver.domain.matching.domain.CompletedMatching;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.dto.request.RecruitmentCreateRequest;
import com.becareful.becarefulserver.domain.matching.repository.CompletedMatchingRepository;
import com.becareful.becarefulserver.domain.matching.repository.RecruitmentRepository;
import com.becareful.becarefulserver.domain.matching.service.CaregiverMatchingService;
import com.becareful.becarefulserver.domain.matching.service.SocialWorkerMatchingService;
import com.becareful.becarefulserver.domain.socialworker.domain.CareLevel;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.repository.ElderlyRepository;
import com.becareful.becarefulserver.fixture.NursingInstitutionFixture;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CaregiverServiceTest extends IntegrationTest {

    @Autowired
    private CaregiverService caregiverService;

    @Autowired
    private CaregiverRepository caregiverRepository;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private WorkApplicationService workApplicationService;

    @Autowired
    private CareerService careerService;

    @Autowired
    private SocialWorkerMatchingService socialWorkerMatchingService;

    @Autowired
    private CaregiverMatchingService caregiverMatchingService;

    @Autowired
    private ElderlyRepository elderlyRepository;

    @Autowired
    private CompletedMatchingRepository completedMatchingRepository;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private WorkApplicationRepository workApplicationRepository;

    @Test
    @WithCaregiver(phoneNumber = "01099990000")
    void 요양보호사_회원탈퇴에_성공한다() {
        // given
        // 010 9999 0000 요양보호사는 Integration Test 에서 세팅
        Long id = caregiverRepository
                .findByPhoneNumber("01099990000")
                .orElseThrow()
                .getId();

        // when
        caregiverService.deleteCaregiver();

        // then
        em.flush();
        em.clear();

        List<Caregiver> caregivers = em.createNativeQuery(
                        """
                            SELECT *
                              FROM caregiver c
                             WHERE c.caregiver_id = ?
                         """,
                        Caregiver.class)
                .setParameter(1, id)
                .getResultList();

        Assertions.assertThat(caregivers).hasSize(1);
        Caregiver caregiver = caregivers.get(0);

        Assertions.assertThat(caregiver.isDeleted()).isTrue();
        Assertions.assertThat(caregiver.getDeleteDate()).isNotNull();
        Assertions.assertThat(caregiver.getName()).isNull();
        Assertions.assertThat(caregiver.getBirthDate()).isNull();
        Assertions.assertThat(caregiver.getGender()).isNull();
        Assertions.assertThat(caregiver.getPhoneNumber()).isNull();
        Assertions.assertThat(caregiver.getAddress()).isNull();
        Assertions.assertThat(caregiver.getProfileImageUrl()).isNull();
        Assertions.assertThat(caregiver.getId()).isNotNull();
    }

    @Test
    @WithCaregiver(phoneNumber = "01099990000")
    void 요양보호사_회원탈퇴시_관련데이터도_모두_삭제된다() {
        // given
        // 010 9999 0000 요양보호사는 Integration Test 에서 세팅
        Caregiver caregiver =
                caregiverRepository.findByPhoneNumber("01099990000").orElseThrow();

        WorkApplicationCreateOrUpdateRequest workRequest = new WorkApplicationCreateOrUpdateRequest(
                List.of(Location.of("서울시", "마포구", "상수동")),
                List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
                List.of(WorkTime.MORNING),
                List.of(CareType.식사보조),
                WorkSalaryUnitType.DAY,
                10000);
        workApplicationService.createOrUpdateWorkApplication(workRequest);
        Long workApplicationId = workApplicationRepository
                .findByCaregiver(caregiver)
                .orElseThrow()
                .getId();

        CareerDetailUpdateRequest careerDetailRequest = new CareerDetailUpdateRequest("institution", "3years");
        CareerCreateOrUpdateRequest careerRequest =
                new CareerCreateOrUpdateRequest("title", CareerType.경력, "introduce", List.of(careerDetailRequest));
        careerService.createOrUpdateCareer(careerRequest);

        Elderly elderly = createElderly("어르신", Location.of("서울시", "마포구", "상수동"));
        RecruitmentCreateRequest recruitmentRequest = new RecruitmentCreateRequest(
                elderly.getId(),
                "title",
                List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                List.of(CareType.식사보조),
                WorkSalaryUnitType.DAY,
                10000,
                "desc");
        Long recruitmentId = socialWorkerMatchingService.createRecruitment(recruitmentRequest);
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId).orElseThrow();
        caregiverMatchingService.applyRecruitment(recruitmentId);

        completedMatchingRepository.save(new CompletedMatching(caregiver, null, recruitment));

        em.flush();
        em.clear();

        // when
        caregiverService.deleteCaregiver();

        // then
        em.flush();
        em.clear();

        List<Caregiver> caregivers = em.createNativeQuery(
                        """
                            SELECT *
                              FROM caregiver c
                             WHERE c.caregiver_id = ?
                         """,
                        Caregiver.class)
                .setParameter(1, caregiver.getId())
                .getResultList();

        Assertions.assertThat(caregivers).hasSize(1);
        Caregiver deletedCaregiver = caregivers.get(0);

        Optional<WorkApplication> deletedWorkApplication = workApplicationRepository.findByCaregiver(deletedCaregiver);
        Assertions.assertThat(deletedWorkApplication.isEmpty()).isTrue();

        List<Application> applications = em.createNativeQuery(
                        """
                            SELECT *
                              FROM application a
                             WHERE a.work_application_id = ?
                         """,
                        Application.class)
                .setParameter(1, workApplicationId)
                .getResultList();
        Assertions.assertThat(applications).isEmpty();

        Assertions.assertThat(completedMatchingRepository.findByCaregiver(deletedCaregiver))
                .isEmpty();

        Assertions.assertThat(deletedCaregiver.isDeleted()).isTrue();
        Assertions.assertThat(deletedCaregiver.getDeleteDate()).isNotNull();
        Assertions.assertThat(deletedCaregiver.getName()).isNull();
        Assertions.assertThat(deletedCaregiver.getBirthDate()).isNull();
        Assertions.assertThat(deletedCaregiver.getGender()).isNull();
        Assertions.assertThat(deletedCaregiver.getPhoneNumber()).isNull();
        Assertions.assertThat(deletedCaregiver.getAddress()).isNull();
        Assertions.assertThat(deletedCaregiver.getProfileImageUrl()).isNull();
        Assertions.assertThat(deletedCaregiver.getId()).isNotNull();
    }

    private Elderly createElderly(String name, Location location) {
        return elderlyRepository.save(Elderly.create(
                name,
                LocalDate.of(1950, 1, 1),
                Gender.FEMALE,
                location,
                "상세",
                false,
                false,
                null,
                NursingInstitutionFixture.NURSING_INSTITUTION,
                CareLevel.일등급,
                "건강",
                EnumSet.of(DetailCareType.스스로식사가능)));
    }
}
