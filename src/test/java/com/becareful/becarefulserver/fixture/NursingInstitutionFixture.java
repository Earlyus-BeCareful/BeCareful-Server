package com.becareful.becarefulserver.fixture;

import com.becareful.becarefulserver.domain.socialworker.domain.NursingInstitution;

import java.time.LocalDateTime;

public class NursingInstitutionFixture {

    public static NursingInstitution NURSING_INSTITUTION =
            NursingInstitution.create(
                    "123",
                    "행복요양기관",
                    "도로명주소",
                    "상세주소",
                    "전화번호",
                    true,
                    LocalDateTime.of(2000,1,1, 0, 0, 0),
                    "url"
            );
}
