package com.becareful.becarefulserver.fixture;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkSalaryUnitType;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkTime;
import com.becareful.becarefulserver.domain.caregiver.domain.vo.CaregiverInfo;
import com.becareful.becarefulserver.domain.caregiver.dto.request.WorkApplicationCreateOrUpdateRequest;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.common.domain.DetailCareType;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.common.domain.vo.Location;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.dto.request.RecruitmentCreateRequest;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.CareLevel;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;

public class MatchingFixture {

    public static Caregiver createCaregiver(String phoneNumber) {
        return Caregiver.create(
                "caregiver",
                LocalDate.of(1990, 1, 1),
                Gender.FEMALE,
                phoneNumber,
                null,
                "서울시",
                "상세주소",
                new CaregiverInfo(false, false, null, null, null),
                true);
    }

    public static Elderly createElderly(NursingInstitution institution) {
        return Elderly.create(
                "어르신",
                LocalDate.of(1950, 1, 1),
                Gender.FEMALE,
                Location.of("서울시", "종로구", "청운동"),
                "상세주소",
                false,
                false,
                null,
                institution,
                CareLevel.일등급,
                "건강",
                EnumSet.of(DetailCareType.스스로식사가능));
    }

    public static Recruitment createRecruitment(String title, Elderly elderly) {
        RecruitmentCreateRequest request = new RecruitmentCreateRequest(
                elderly.getId(),
                title,
                List.of(DayOfWeek.MONDAY),
                LocalTime.of(9, 0),
                LocalTime.of(11, 0),
                List.of(CareType.식사보조),
                WorkSalaryUnitType.DAY,
                10000,
                "description");
        return Recruitment.create(request, elderly);
    }

    public static WorkApplication createWorkApplication(Caregiver caregiver) {

        WorkApplicationCreateOrUpdateRequest workRequest = new WorkApplicationCreateOrUpdateRequest(
                List.of(Location.of("서울시", "종로구", "청운동")),
                List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
                List.of(WorkTime.MORNING),
                List.of(CareType.식사보조),
                WorkSalaryUnitType.DAY,
                10000);

        return WorkApplication.create(workRequest, caregiver);
    }

    public static Matching createMatching(Recruitment recruitment, WorkApplication workApplication) {
        return Matching.create(recruitment, workApplication);
    }
}
