package com.becareful.becarefulserver.domain.work_location.dto.request;

import com.becareful.becarefulserver.domain.work_location.domain.WorkLocation;

// TODO : Location VO 로 대체
public record WorkLocationDto(String siDo, String siGuGun, String dongEupMyeon) {
    public static WorkLocationDto from(WorkLocation workLocation) {
        return new WorkLocationDto(
                workLocation.getLocation().getSiDo(),
                workLocation.getLocation().getSiGuGun(),
                workLocation.getLocation().getEupMyeonDong());
    }
}
