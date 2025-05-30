package com.becareful.becarefulserver.domain.work_location.dto.request;

import com.becareful.becarefulserver.domain.work_location.domain.WorkLocation;

public record WorkLocationDto(String siDo, String siGuGun, String dongEupMyeon) {
    public static WorkLocationDto from(WorkLocation workLocation) {
        return new WorkLocationDto(workLocation.getSiDo(), workLocation.getSiGuGun(), workLocation.getEupMyeonDong());
    }
}
