package com.becareful.becarefulserver.domain.work_location.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import com.becareful.becarefulserver.domain.work_location.dto.request.WorkLocationDto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkLocation {

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
}
