package com.becareful.becarefulserver.matching.caregiver;

import com.becareful.becarefulserver.common.IntegrationTest;
import com.becareful.becarefulserver.common.WithCaregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.CareerType;
import com.becareful.becarefulserver.domain.caregiver.dto.request.CareerDetailUpdateRequest;
import com.becareful.becarefulserver.domain.caregiver.dto.request.CareerUpdateRequest;
import com.becareful.becarefulserver.domain.caregiver.service.CareerService;
import com.becareful.becarefulserver.domain.caregiver.service.CaregiverService;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CaregiverTest extends IntegrationTest {

    @Autowired
    private CaregiverService caregiverService;

    @Autowired
    private CareerService careerService;

    @Nested
    class 마이페이지_조회시 {

        @Test
        @WithCaregiver(phoneNumber = "01099990000")
        void 경력서를_작성하지_않았다면_null을_반환한다() {
            // given

            // when
            var response = caregiverService.getCaregiverMyPageHomeData();

            // then
            Assertions.assertThat(response.careerInfo()).isNull();
        }

        @Test
        @WithCaregiver(phoneNumber = "01099990000")
        void 신입_경력서를_반환한다() {
            // given
            careerService.updateCareer(new CareerUpdateRequest("경력서 제목", CareerType.신입, "잘 부탁드립니다.", List.of()));

            // when
            var response = caregiverService.getCaregiverMyPageHomeData();

            // then
            Assertions.assertThat(response.careerInfo()).hasFieldOrProperty("careerId");
            Assertions.assertThat(response.careerInfo()).hasFieldOrProperty("title");
            Assertions.assertThat(response.careerInfo()).hasFieldOrPropertyWithValue("careerType", CareerType.신입);
            Assertions.assertThat(response.careerInfo()).hasFieldOrProperty("introduce");
            Assertions.assertThat(response.careerInfo()).hasFieldOrProperty("lastModifiedDate");
            Assertions.assertThat(response.careerInfo()).hasFieldOrPropertyWithValue("careerDetails", List.of());
        }

        @Test
        @WithCaregiver(phoneNumber = "01099990000")
        void 경력_경력서를_반환한다() {
            // given
            careerService.updateCareer(new CareerUpdateRequest(
                    "경력서 제목",
                    CareerType.경력,
                    "잘 부탁드립니다.",
                    List.of(new CareerDetailUpdateRequest("전주기관", "3년"), new CareerDetailUpdateRequest("서울기관", "2년"))));

            // when
            var response = caregiverService.getCaregiverMyPageHomeData();

            // then
            Assertions.assertThat(response.careerInfo()).hasFieldOrProperty("careerId");
            Assertions.assertThat(response.careerInfo()).hasFieldOrProperty("title");
            Assertions.assertThat(response.careerInfo()).hasFieldOrPropertyWithValue("careerType", CareerType.경력);
            Assertions.assertThat(response.careerInfo()).hasFieldOrProperty("introduce");
            Assertions.assertThat(response.careerInfo()).hasFieldOrProperty("lastModifiedDate");
            Assertions.assertThat(response.careerInfo().careerDetails()).hasSize(2);
        }
    }
}
