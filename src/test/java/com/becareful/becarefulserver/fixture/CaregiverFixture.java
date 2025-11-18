package com.becareful.becarefulserver.fixture;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.vo.CaregiverInfo;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import java.time.LocalDate;

public class CaregiverFixture {
    public static Caregiver createCaregiver() {
        return Caregiver.create(
                "caregiver",
                LocalDate.of(1990, 1, 1),
                Gender.FEMALE,
                "01099990000",
                null,
                "서울시",
                "상세주소",
                new CaregiverInfo(false, false, null, null, null),
                true);
    }
}
