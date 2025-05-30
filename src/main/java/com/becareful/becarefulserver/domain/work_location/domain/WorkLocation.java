package com.becareful.becarefulserver.domain.work_location.domain;

import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.ResidentialAddress;
import com.becareful.becarefulserver.domain.work_location.dto.request.WorkLocationDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkLocation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_location_id")
    private Long id;

    private String siDo;

    private String siGuGun;

    private String eupMyeonDong;

    @Builder(access = AccessLevel.PRIVATE)
    private WorkLocation(String siDo, String siGuGun, String eupMyeonDong) {
        this.siDo = siDo;
        this.siGuGun = siGuGun;
        this.eupMyeonDong = eupMyeonDong;
    }

    public static WorkLocation from(WorkLocationDto workLocationDto) {
        return WorkLocation.builder()
                .siDo(workLocationDto.siDo())
                .siGuGun(workLocationDto.siGuGun())
                .eupMyeonDong(workLocationDto.dongEupMyeon())
                .build();
    }

    public Double calculateMatchingRate(ResidentialAddress address) {
        if (!address.getSiDo().equals(siDo)) {
            return 0.0;
        }

        if (siGuGun.equals("전체") || address.getSiGuGun().equals("전체") || siGuGun.equals(address.getSiGuGun())) {
            if (eupMyeonDong.equals("전체")
                    || address.getEupMyeonDong().equals("전체")
                    || eupMyeonDong.equals(address.getEupMyeonDong())) {
                return 100.0;
            }
            return 50.0;
        }

        return 0.0;
    }
}
